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
    static {
        SET_CODE_TO_NAME.put("LOB", "Legend of Blue Eyes White Dragon");
        SET_CODE_TO_NAME.put("MRD", "Metal Raiders");
        SET_CODE_TO_NAME.put("SRL", "Spell Ruler");
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
        // Convertir le code abrégé en nom complet
        String setName = SET_CODE_TO_NAME.getOrDefault(setCode, setCode);

        // Utiliser UriComponentsBuilder pour construire l'URL, puis dé-encoder %20 en espaces
        String url = UriComponentsBuilder.fromHttpUrl(YGOPRODECK_API_URL + "/cardinfo.php")
                .queryParam("cardset", "{setName}")
                .buildAndExpand(setName)
                .toUriString();
        logger.info("URL construite avant envoi : {}", url); // Log pour vérification

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

            // Sauvegarder le set
            int totalCards = cards.size();
            YugiohSet yugiohSet = yugiohSetService.saveSet(setCode, totalCards);

            // Récupérer et associer la série
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

            // Sauvegarder la traduction anglaise du set
            String setNameEn = cards.get(0).path("card_sets").get(0).path("set_name").asText(setCode);
            yugiohSetService.saveSetTranslation(yugiohSet.getId(), "en", setNameEn);

            // Traiter les cartes
            for (JsonNode cardNode : cards) {
                String cardId = cardNode.path("id").asText();
                Long cardIdInDb = yugiohCardService.saveYugiohCard(cardId, setCode);

                String imageUrl = cardNode.path("card_images").get(0).path("image_url").asText();
                imageDownloader.downloadImage(imageUrl, cardId, IMAGE_DIRECTORY);

                String nameEn = cardNode.path("name").asText();
                yugiohCardService.saveCardTranslation(cardIdInDb, "en", nameEn);
            }
        } catch (IOException e) {
            logger.error("Erreur lors du traitement du set Yu-Gi-Oh {} : {}", setName, e.getMessage());
            throw new IllegalStateException("Erreur lors du traitement du set " + setName + ": " + e.getMessage());
        }
    }
}