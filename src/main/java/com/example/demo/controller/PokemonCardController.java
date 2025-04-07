package com.example.demo.controller;

import com.example.demo.dto.SetDTO;
import com.example.demo.dto.SetTranslationDTO;
import com.example.demo.entity.PokemonCard;
import com.example.demo.service.PokemonCardService;
import com.example.demo.service.PokemonDataService;
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
            String response = restTemplate.getForObject(url, String.class);
            if (response == null) {
                logger.error("Aucune réponse de l'API Pokémon TCG");
                return sets;
            }
            JsonNode root = objectMapper.readTree(response);
            JsonNode setsNode = root.path("data");
            for (JsonNode setNode : setsNode) {
                String setCode = setNode.path("id").asText();
                String setName = setNode.path("name").asText();
                sets.add(createSetDTO(setCode, setName, "Pokemon", setCode.hashCode() % 1000L));
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des sets Pokémon : {}", e.getMessage());
        }
        logger.info("Retour de {} sets Pokémon", sets.size());
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