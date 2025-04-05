package com.example.demo.service;

import com.example.demo.entity.PokemonSet;
import com.example.demo.image.ImageDownloader;
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

@Service
public class PokemonDataService {

    private static final Logger logger = LoggerFactory.getLogger(PokemonDataService.class);
    private static final String POKEMON_TCG_API_URL = "https://api.pokemontcg.io/v2";
    private static final String IMAGE_DIRECTORY = "images/Pokemon"; // Répertoire par défaut

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ImageDownloader imageDownloader;

    @Autowired
    private PokemonCardService pokemonCardService;

    public void processPokemonSet(String setCode) {
        String url = POKEMON_TCG_API_URL + "/cards?q=set.id:" + setCode;
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode cards = root.path("data");

            PokemonSet pokemonSet = pokemonCardService.saveSet(setCode, cards.size());

            for (JsonNode cardNode : cards) {
                String cardId = cardNode.path("id").asText();
                String hp = cardNode.path("hp").asText("Unknown");
                String energyType = cardNode.path("types").get(0) != null ? cardNode.path("types").get(0).asText() : "Unknown";
                String weakness = cardNode.path("weaknesses").get(0) != null ? cardNode.path("weaknesses").get(0).path("type").asText() : "Unknown";

                Long cardIdInDb = pokemonCardService.savePokemonCard(cardId, setCode, hp, energyType, weakness);

                String imageUrl = cardNode.path("images").path("large").asText();
                imageDownloader.downloadImage(imageUrl, cardId, IMAGE_DIRECTORY); // Ajout du répertoire

                Map<String, String> translations = new HashMap<>();
                translations.put("en", cardNode.path("name").asText());

                String enFlavorText = cardNode.path("flavorText").asText("No flavor text available");
                pokemonCardService.saveCardTranslation(cardIdInDb, "en", translations.get("en"), enFlavorText);
            }
        } catch (IOException e) {
            logger.error("Erreur lors du traitement du set Pokémon {} : {}", setCode, e.getMessage());
        }
    }
}