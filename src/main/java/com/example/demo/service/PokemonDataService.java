package com.example.demo.service;

import com.example.demo.entity.PokemonSet;
import com.example.demo.entity.Serie;
import com.example.demo.image.ImageDownloader;
import com.example.demo.repository.PokemonSetRepository;
import com.example.demo.translation.PokemonCardTranslation;
import com.example.demo.translation.PokemonSetTranslation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PokemonDataService {

    private static final Logger logger = LoggerFactory.getLogger(PokemonDataService.class);
    private static final String POKEMON_TCG_API_URL = "https://api.pokemontcg.io/v2";
    private static final String TCGDEX_API_URL = "https://api.tcgdex.net/v2";
    private static final String IMAGE_DIRECTORY = "images/Pokemon";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ImageDownloader imageDownloader;

    @Autowired
    private PokemonCardService pokemonCardService;

    @Autowired
    private PokemonSetService pokemonSetService;

    @Autowired
    private SerieService serieService;

    @Autowired
    private PokemonSetRepository pokemonSetRepository;

    public void processPokemonSet(String setCode) {
        // Étape 1 : Récupérer les données depuis l'API Pokémon TCG
        String tcgUrl = POKEMON_TCG_API_URL + "/cards?q=set.id:" + setCode;
        String tcgResponse = restTemplate.getForObject(tcgUrl, String.class);

        try {
            JsonNode root = objectMapper.readTree(tcgResponse);
            JsonNode cards = root.path("data");

            // Sauvegarder le set
            int totalCards = cards.size();
            PokemonSet pokemonSet = pokemonSetService.saveSet(setCode, totalCards);

            // Récupérer et associer la série
            JsonNode setNode = cards.get(0).path("set");
            String serieName = setNode.path("series").asText("Unknown Series");
            Optional<Serie> serieOptional = serieService.findByNameAndGameType(serieName, "Pokemon");
            Serie serie;
            if (serieOptional.isEmpty()) {
                serie = serieService.saveSerie("Pokemon", serieName);
            } else {
                serie = serieOptional.get();
            }
            pokemonSet.setSerie(serie);
            pokemonSetRepository.save(pokemonSet);

            // Sauvegarder la traduction anglaise du set
            String setNameEn = setNode.path("name").asText(setCode);
            pokemonSetService.saveSetTranslation(pokemonSet.getId(), "en", setNameEn);

            // Étape 2 : Récupérer les traductions non-EN depuis TCGdex pour le set
            String tcgdexSetUrl = TCGDEX_API_URL + "/en/sets/" + setCode;
            String tcgdexSetResponse = restTemplate.getForObject(tcgdexSetUrl, String.class);
            if (tcgdexSetResponse != null) {
                JsonNode tcgdexSetNode = objectMapper.readTree(tcgdexSetResponse);
                String setNameFr = fetchTranslationFromTcgdex(setCode, "fr", "sets");
                if (setNameFr != null) {
                    pokemonSetService.saveSetTranslation(pokemonSet.getId(), "fr", setNameFr);
                }
            }

            // Traiter les cartes
            for (JsonNode cardNode : cards) {
                String cardId = cardNode.path("id").asText();
                String hp = cardNode.path("hp").asText("Unknown");
                String energyType = cardNode.path("types").get(0) != null ? cardNode.path("types").get(0).asText() : "Unknown";
                String weakness = cardNode.path("weaknesses").get(0) != null ? cardNode.path("weaknesses").get(0).path("type").asText() : "Unknown";

                Long cardIdInDb = pokemonCardService.savePokemonCard(cardId, setCode, hp, energyType, weakness);

                String imageUrl = cardNode.path("images").path("large").asText();
                imageDownloader.downloadImage(imageUrl, cardId, IMAGE_DIRECTORY);

                // Traduction anglaise depuis Pokémon TCG
                String nameEn = cardNode.path("name").asText();
                String flavorTextEn = cardNode.path("flavorText").asText("No flavor text available");
                pokemonCardService.saveCardTranslation(cardIdInDb, "en", nameEn, flavorTextEn);

                // Traduction française (ou autre) depuis TCGdex
                String tcgdexCardUrl = TCGDEX_API_URL + "/en/cards/" + cardId;
                String tcgdexCardResponse = restTemplate.getForObject(tcgdexCardUrl, String.class);
                if (tcgdexCardResponse != null) {
                    JsonNode tcgdexCardNode = objectMapper.readTree(tcgdexCardResponse);
                    String nameFr = fetchTranslationFromTcgdex(cardId, "fr", "cards");
                    if (nameFr != null) {
                        String flavorTextFr = tcgdexCardNode.path("flavorText").asText("No flavor text available");
                        pokemonCardService.saveCardTranslation(cardIdInDb, "fr", nameFr, flavorTextFr);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Erreur lors du traitement du set Pokémon {} : {}", setCode, e.getMessage());
        }
    }

    // Méthode utilitaire pour récupérer les traductions depuis TCGdex
    private String fetchTranslationFromTcgdex(String id, String languageCode, String type) {
        try {
            String url = TCGDEX_API_URL + "/" + languageCode + "/" + type + "/" + id;
            String response = restTemplate.getForObject(url, String.class);
            if (response != null) {
                JsonNode node = objectMapper.readTree(response);
                return node.path("name").asText(null);
            }
        } catch (IOException e) {
            logger.warn("Erreur lors de la récupération de la traduction {} pour {} {} : {}", languageCode, type, id, e.getMessage());
        }
        return null;
    }
}