package com.example.demo.service;

import com.example.demo.entity.PokemonSet;
import com.example.demo.entity.Serie;
import com.example.demo.image.ImageDownloader;
import com.example.demo.repository.PokemonSetRepository;
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
    private PokemonSetService pokemonSetService; // Ajout ici

    @Autowired
    private SerieService serieService;

    public void processPokemonSet(String setCode) {
        String url = POKEMON_TCG_API_URL + "/cards?q=set.id:" + setCode;
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode cards = root.path("data");

            // Sauvegarder le set
            int totalCards = cards.size();
            PokemonSet pokemonSet = pokemonSetService.saveSet(setCode, totalCards);

            // Récupérer et associer la série (si disponible dans l'API)
            JsonNode setNode = cards.get(0).path("set"); // Supposons que le premier carte contient les infos du set
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

            // Sauvegarder la traduction du set
            String setName = setNode.path("name").asText(setCode); // Nom du set depuis l'API
            pokemonSetService.saveSetTranslation(pokemonSet.getId(), "en", setName);

            // Traiter les cartes
            for (JsonNode cardNode : cards) {
                String cardId = cardNode.path("id").asText();
                String hp = cardNode.path("hp").asText("Unknown");
                String energyType = cardNode.path("types").get(0) != null ? cardNode.path("types").get(0).asText() : "Unknown";
                String weakness = cardNode.path("weaknesses").get(0) != null ? cardNode.path("weaknesses").get(0).path("type").asText() : "Unknown";

                Long cardIdInDb = pokemonCardService.savePokemonCard(cardId, setCode, hp, energyType, weakness);

                String imageUrl = cardNode.path("images").path("large").asText();
                imageDownloader.downloadImage(imageUrl, cardId, IMAGE_DIRECTORY);

                Map<String, String> translations = new HashMap<>();
                translations.put("en", cardNode.path("name").asText());

                String enFlavorText = cardNode.path("flavorText").asText("No flavor text available");
                pokemonCardService.saveCardTranslation(cardIdInDb, "en", translations.get("en"), enFlavorText);
            }
        } catch (IOException e) {
            logger.error("Erreur lors du traitement du set Pokémon {} : {}", setCode, e.getMessage());
        }
    }

    // Getter temporaire pour PokemonSetRepository (à refactoriser si nécessaire)
    private PokemonSetRepository pokemonSetRepository;

    @Autowired
    public void setPokemonSetRepository(PokemonSetRepository pokemonSetRepository) {
        this.pokemonSetRepository = pokemonSetRepository;
    }
}