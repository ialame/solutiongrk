package com.example.demo.controller;

import com.example.demo.dto.SetDTO;
import com.example.demo.dto.SetTranslationDTO;
import com.example.demo.entity.YugiohCard;
import com.example.demo.service.YugiohCardService;
import com.example.demo.service.YugiohDataService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
            String url = "https://db.ygoprodeck.com/api/v7/cardsets";
            String response = restTemplate.getForObject(url, String.class);
            if (response == null) {
                logger.error("Aucune réponse de l'API YGOPRODECK");
                return sets;
            }
            JsonNode root = objectMapper.readTree(response);
            for (JsonNode setNode : root) {
                String setCode = setNode.path("set_code").asText();
                String setName = setNode.path("set_name").asText();
                sets.add(createSetDTO(setCode, setName, "Yugioh", setCode.hashCode() % 1000L));
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des sets Yu-Gi-Oh : {}", e.getMessage());
        }
        logger.info("Retour de {} sets Yu-Gi-Oh", sets.size());
        return sets;
    }

    private SetDTO createSetDTO(String setCode, String name, String gameType, Long serieId) {
        SetDTO setDTO = new SetDTO();
        setDTO.setSetCode(setCode);
        setDTO.setGameType(gameType);
        setDTO.setSerieId(serieId);

        SetTranslationDTO translation = new SetTranslationDTO();
        translation.setName(name);
        translation.setLanguage("en");

        setDTO.setTranslations(Arrays.asList(translation));
        return setDTO;
    }
}