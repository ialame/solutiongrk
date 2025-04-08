package com.example.demo.controller;

import com.example.demo.dto.SetDTO;
import com.example.demo.dto.SetTranslationDTO;
import com.example.demo.dto.LanguageDTO;
import com.example.demo.entity.YugiohCard;
import com.example.demo.service.YugiohCardService;
import com.example.demo.service.YugiohDataService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class YugiohCardController {

    private static final Logger logger = LoggerFactory.getLogger(YugiohCardController.class);

    @Autowired
    private YugiohCardService yugiohCardService;

    @Autowired
    private YugiohDataService yugiohDataService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/yugioh-cards")
    public List<YugiohCard> getAllCards() {
        logger.info("Requête GET /api/yugioh-cards reçue");
        List<YugiohCard> cards = yugiohCardService.findAllCards();
        logger.info("Retour de {} cartes Yu-Gi-Oh", cards.size());
        return cards;
    }

    @PostMapping("/yugioh-sets/process/{setCode}")
    public void processYugiohSet(@PathVariable String setCode) {
        logger.info("Requête POST /api/yugioh-sets/process/{} reçue", setCode);
        yugiohDataService.processYugiohSet(setCode);
        logger.info("Traitement de la série Yu-Gi-Oh {} terminé", setCode);
    }

    @GetMapping("/yugioh-sets")
    public List<SetDTO> getYugiohSets() {
        logger.info("Requête GET /api/yugioh-sets reçue");
        List<SetDTO> sets = new ArrayList<>();
        try {
            String url = "https://ygoprodeck.com/api/v8/cardsets";
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Erreur lors de la récupération des sets : {}", response.getStatusCode());
                return getStaticYugiohSets();
            }

            String responseBody = response.getBody();
            if (responseBody == null) {
                logger.error("Aucune réponse de l'API YGOPRODECK");
                return getStaticYugiohSets();
            }

            JsonNode root = objectMapper.readTree(responseBody);
            for (JsonNode setNode : root) {
                String setCode = setNode.path("set_code").asText();
                String setName = setNode.path("set_name").asText();
                sets.add(createSetDTO(setCode, setName, "Yugioh", setCode.hashCode() % 1000L));
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des sets Yu-Gi-Oh : {}", e.getMessage());
            return getStaticYugiohSets();
        }
        logger.info("Retour de {} sets Yu-Gi-Oh", sets.size());
        return sets;
    }

    private List<SetDTO> getStaticYugiohSets() {
        logger.info("Utilisation de la liste statique des sets Yu-Gi-Oh");
        return Arrays.asList(
                createSetDTO("LOB", "Legend of Blue Eyes White Dragon", "Yugioh", 1L),
                createSetDTO("MRD", "Metal Raiders", "Yugioh", 2L),
                createSetDTO("SRL", "Spell Ruler", "Yugioh", 3L),
                createSetDTO("PSV", "Pharaoh's Servant", "Yugioh", 4L),
                createSetDTO("LON", "Labyrinth of Nightmare", "Yugioh", 5L)
        );
    }

    private SetDTO createSetDTO(String setCode, String name, String gameType, Long serieId) {
        SetDTO setDTO = new SetDTO();
        setDTO.setSetCode(setCode);
        setDTO.setGameType(gameType);
        setDTO.setSerieId(serieId);

        SetTranslationDTO translation = new SetTranslationDTO();
        translation.setName(name);
        LanguageDTO language = new LanguageDTO();
        language.setCode("en");
        translation.setLanguage(language);

        setDTO.setTranslations(Arrays.asList(translation));
        return setDTO;
    }
}