package com.example.demo.controller;

import com.example.demo.dto.SetDTO;
import com.example.demo.dto.SetTranslationDTO;
import com.example.demo.dto.LanguageDTO;
import com.example.demo.entity.PokemonCard;
import com.example.demo.service.PokemonCardService;
import com.example.demo.service.PokemonDataService;
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
public class PokemonCardController {

    private static final Logger logger = LoggerFactory.getLogger(PokemonCardController.class);

    @Autowired
    private PokemonCardService pokemonCardService;

    @Autowired
    private PokemonDataService pokemonDataService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/pokemon-cards")
    public List<PokemonCard> getAllCards() {
        logger.info("Requête GET /api/pokemon-cards reçue");
        List<PokemonCard> cards = pokemonCardService.findAllCards();
        logger.info("Retour de {} cartes Pokémon", cards.size());
        return cards;
    }

    @PostMapping("/pokemon-sets/process/{setCode}")
    public void processPokemonSet(@PathVariable String setCode) {
        logger.info("Requête POST /api/pokemon-sets/process/{} reçue", setCode);
        pokemonDataService.processPokemonSet(setCode);
        logger.info("Traitement de la série Pokémon {} terminé", setCode);
    }

    @GetMapping("/pokemon-sets")
    public List<SetDTO> getPokemonSets() {
        logger.info("Requête GET /api/pokemon-sets reçue");
        List<SetDTO> sets = new ArrayList<>();
        try {
            String url = "https://api.pokemontcg.io/v2/sets";
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Erreur lors de la récupération des sets : {}", response.getStatusCode());
                return getStaticPokemonSets();
            }

            String responseBody = response.getBody();
            if (responseBody == null) {
                logger.error("Aucune réponse de l'API Pokémon TCG");
                return getStaticPokemonSets();
            }

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode setsNode = root.path("data");
            for (JsonNode setNode : setsNode) {
                String setCode = setNode.path("id").asText();
                String setName = setNode.path("name").asText();
                sets.add(createSetDTO(setCode, setName, "Pokemon", setCode.hashCode() % 1000L));
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des sets Pokémon : {}", e.getMessage());
            return getStaticPokemonSets();
        }
        logger.info("Retour de {} sets Pokémon", sets.size());
        return sets;
    }

    private List<SetDTO> getStaticPokemonSets() {
        logger.info("Utilisation de la liste statique des sets Pokémon");
        return Arrays.asList(
                createSetDTO("base1", "Base Set", "Pokemon", 4L),
                createSetDTO("base2", "Jungle", "Pokemon", 5L),
                createSetDTO("base3", "Fossil", "Pokemon", 6L)
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