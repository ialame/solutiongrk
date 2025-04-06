package com.example.demo.service;

import com.example.demo.entity.YugiohSet;
import com.example.demo.entity.Serie;
import com.example.demo.image.ImageDownloader;
import com.example.demo.repository.YugiohSetRepository;
import com.example.demo.translation.YugiohCardTranslation;
import com.example.demo.translation.YugiohSetTranslation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class YugiohDataService {

    private static final Logger logger = LoggerFactory.getLogger(YugiohDataService.class);
    private static final String YGOPRODECK_API_URL = "https://db.ygoprodeck.com/api/v7";
    private static final String IMAGE_DIRECTORY = "images/Yugioh";

    private static final Map<String, String> SET_CODE_TO_NAME = new HashMap<>();
    private static final Map<String, Map<String, String>> CARD_TRANSLATIONS = new HashMap<>();
    private static final Map<String, Map<String, String>> SET_TRANSLATIONS = new HashMap<>();
    static {
        SET_CODE_TO_NAME.put("LOB", "Legend of Blue Eyes White Dragon");
        SET_CODE_TO_NAME.put("MRD", "Metal Raiders");
        SET_CODE_TO_NAME.put("SRL", "Spell Ruler");

        Map<String, String> blueEyesTranslations = new HashMap<>();
        blueEyesTranslations.put("en", "Blue-Eyes White Dragon");
        blueEyesTranslations.put("fr", "Dragon Blanc aux Yeux Bleus");
        CARD_TRANSLATIONS.put("89631139", blueEyesTranslations);

        Map<String, String> darkMagicianTranslations = new HashMap<>();
        darkMagicianTranslations.put("en", "Dark Magician");
        darkMagicianTranslations.put("fr", "Magicien Sombre");
        CARD_TRANSLATIONS.put("46986414", darkMagicianTranslations);

        Map<String, String> lobTranslations = new HashMap<>();
        lobTranslations.put("en", "Legend of Blue Eyes White Dragon");
        lobTranslations.put("fr", "Légende du Dragon Blanc aux Yeux Bleus");
        SET_TRANSLATIONS.put("LOB", lobTranslations);
    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ImageDownloader imageDownloader;

    @Autowired
    private YugiohCardService yugiohCardService;

    @Autowired
    private YugiohSetService yugiohSetService;

    @Autowired
    private SerieService serieService;

    @Autowired
    private YugiohSetRepository yugiohSetRepository;

    public void processYugiohSet(String setCode) {
        String setName = SET_CODE_TO_NAME.getOrDefault(setCode, setCode);
        String url = UriComponentsBuilder.fromHttpUrl(YGOPRODECK_API_URL + "/cardinfo.php")
                .queryParam("cardset", setName)
                .toUriString()
                .replace("%20", " "); // Correction pour RestTemplate

        logger.info("URL construite avant envoi : {}", url);

        String response;

        try {
            response = restTemplate.getForObject(url, String.class);
            if (response == null) {
                logger.warn("Aucune donnée retournée pour le set Yu-Gi-Oh {}", setName);
                throw new IllegalStateException("Aucune donnée retournée pour le set " + setName);
            }
        } catch (HttpClientErrorException e) {
            logger.error("Erreur HTTP lors de la récupération du set Yu-Gi-Oh {} : {}", setName, e.getMessage());
            throw new IllegalStateException("Erreur lors de la récupération du set " + setName + ": " + e.getMessage());
        }

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode cards = root.path("data");

            if (cards.isEmpty()) {
                logger.warn("Aucune carte trouvée pour le set Yu-Gi-Oh {}", setName);
                throw new IllegalStateException("Aucune carte trouvée pour le set " + setName);
            }

            int totalCards = cards.size();
            YugiohSet yugiohSet = yugiohSetService.saveSet(setCode, totalCards);

            String serieName = setCode.split("-")[0];
            Optional<Serie> serieOptional = serieService.findByNameAndGameType(serieName, "Yugioh");
            Serie serie;
            if (serieOptional.isEmpty()) {
                serie = serieService.saveSerie("Yugioh", serieName);
            } else {
                serie = serieOptional.get();
            }
            yugiohSet.setSerie(serie);
            yugiohSetRepository.save(yugiohSet);

            // Sauvegarder les traductions du set
            Map<String, String> setTranslations = SET_TRANSLATIONS.getOrDefault(setCode, new HashMap<>());
            setTranslations.forEach((lang, name) ->
                    yugiohSetService.saveSetTranslation(yugiohSet.getId(), lang, name));

            for (JsonNode cardNode : cards) {
                String cardId = cardNode.path("id").asText();
                Integer attack = cardNode.path("atk").isMissingNode() ? null : cardNode.path("atk").asInt();
                Integer defense = cardNode.path("def").isMissingNode() ? null : cardNode.path("def").asInt();
                String type = cardNode.path("type").asText();

                Long cardIdInDb = yugiohCardService.saveYugiohCard(cardId, setCode, attack, defense, type);

                String imageUrl = cardNode.path("card_images").get(0).path("image_url").asText();
                imageDownloader.downloadImage(imageUrl, cardId, IMAGE_DIRECTORY);

                // Sauvegarder les traductions de la carte
                Map<String, String> cardTranslations = CARD_TRANSLATIONS.getOrDefault(cardId, new HashMap<>());
                cardTranslations.put("en", cardNode.path("name").asText()); // Ajouter l'anglais depuis l'API
                cardTranslations.forEach((lang, name) ->
                        yugiohCardService.saveCardTranslation(cardIdInDb, lang, name));
            }
        } catch (IOException e) {
            logger.error("Erreur lors du traitement du set Yu-Gi-Oh {} : {}", setName, e.getMessage());
            throw new IllegalStateException("Erreur lors du traitement du set " + setName + ": " + e.getMessage());
        }
    }
}