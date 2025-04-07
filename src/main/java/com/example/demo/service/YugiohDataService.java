package com.example.demo.service;

import com.example.demo.entity.YugiohSet;
import com.example.demo.entity.Serie;
import com.example.demo.image.ImageDownloader;
import com.example.demo.repository.YugiohSetRepository;
import com.example.demo.translation.YugiohCardTranslation;
import com.example.demo.translation.YugiohSetTranslation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class YugiohDataService {

    private static final Logger logger = LoggerFactory.getLogger(YugiohDataService.class);
    private static final String YGOPRODECK_API_URL = "https://db.ygoprodeck.com/api/v7";
    private static final String KONAMI_BASE_URL = "https://www.db.yugioh-card.com/yugiohdb/card_search.action";
    private static final String IMAGE_DIRECTORY = "images/Yugioh";

    private static final Map<String, String> SET_CODE_TO_NAME = new HashMap<>();
    private static final Map<String, String> CARD_ID_TO_CID = new HashMap<>();
    static {
        SET_CODE_TO_NAME.put("LOB", "Legend of Blue Eyes White Dragon");
        SET_CODE_TO_NAME.put("MRD", "Metal Raiders");
        SET_CODE_TO_NAME.put("SRL", "Spell Ruler");

        CARD_ID_TO_CID.put("36304921", "4082"); // Witty Phantom
        CARD_ID_TO_CID.put("59197169", "4091"); // Yami
        CARD_ID_TO_CID.put("89631139", "4007"); // Blue-Eyes White Dragon
        CARD_ID_TO_CID.put("46986414", "4008"); // Dark Magician
        CARD_ID_TO_CID.put("74677422", "4024"); // Red-Eyes Black Dragon
        CARD_ID_TO_CID.put("45042329", "4136"); // Tripwire Beast
        CARD_ID_TO_CID.put("37313348", "4137"); // Turtle Tiger

    }
    private String findCidByEnglishName(String englishName) {
        try {
            String url = KONAMI_BASE_URL + "?ope=1&keyword=" + URLEncoder.encode(englishName, "UTF-8") + "&request_locale=en";
            logger.info("Recherche du CID pour '{}' avec URL : {}", englishName, url);
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();
            String cid = doc.select("a.link_card").first().attr("href").split("cid=")[1].split("&")[0];
            logger.info("CID trouvé pour '{}' : {}", englishName, cid);
            return cid;
        } catch (Exception e) {
            logger.warn("Erreur lors de la recherche du CID pour '{}' : {}", englishName, e.getMessage());
            return null;
        }
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
                .replace("%20", " ");

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

            yugiohSetService.saveSetTranslation(yugiohSet.getId(), "en", setName);
            String frenchSetName = getFrenchTranslationForSet(setCode);
            if (frenchSetName != null) {
                yugiohSetService.saveSetTranslation(yugiohSet.getId(), "fr", frenchSetName);
            }

            for (JsonNode cardNode : cards) {
                String cardId = cardNode.path("id").asText();
                Integer attack = cardNode.path("atk").isMissingNode() ? null : cardNode.path("atk").asInt();
                Integer defense = cardNode.path("def").isMissingNode() ? null : cardNode.path("def").asInt();
                String type = cardNode.path("type").asText();

                Long cardIdInDb = yugiohCardService.saveYugiohCard(cardId, setCode, attack, defense, type);

                String imageUrl = cardNode.path("card_images").get(0).path("image_url").asText();
                imageDownloader.downloadImage(imageUrl, cardId, IMAGE_DIRECTORY);

                String englishName = cardNode.path("name").asText();
                yugiohCardService.saveCardTranslation(cardIdInDb, "en", englishName);

                logger.info("Tentative de récupération de la traduction française pour la carte ID {}", cardId);
                ///////////////////
                String cid = CARD_ID_TO_CID.get(cardId);
                if (cid == null) {
                    cid = findCidByEnglishName(englishName);
                    if (cid != null) {
                        CARD_ID_TO_CID.put(cardId, cid);
                    }
                }
                String frenchName = cid != null ? getFrenchTranslation(cardId) : null;
                ///////////////////
                //String frenchName = getFrenchTranslation(cardId);
                if (frenchName != null) {
                    logger.info("Traduction française trouvée pour la carte ID {} : {}", cardId, frenchName);
                    yugiohCardService.saveCardTranslation(cardIdInDb, "fr", frenchName);
                } else {
                    logger.warn("Aucune traduction française disponible pour la carte ID {}", cardId);
                }
            }
        } catch (IOException e) {
            logger.error("Erreur lors du traitement du set Yu-Gi-Oh {} : {}", setName, e.getMessage());
            throw new IllegalStateException("Erreur lors du traitement du set " + setName + ": " + e.getMessage());
        }
    }

    private String getFrenchTranslation(String cardId) {
        String cid = CARD_ID_TO_CID.get(cardId);
        if (cid == null) {
            logger.warn("CID non trouvé pour la carte ID {}", cardId);
            return null;
        }
        try {
            String url = KONAMI_BASE_URL + "?ope=2&cid=" + cid + "&request_locale=fr";
            logger.info("Tentative de scraping pour la carte ID {} avec URL : {}", cardId, url);
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();
            String frenchName = doc.select("#cardname").text(); // Confirmer que c'est bien #item_name
            logger.info("Nom français récupéré pour la carte ID {} : '{}'", cardId, frenchName);
            if (frenchName.isEmpty()) {
                logger.warn("Aucune traduction française trouvée pour la carte ID {}", cardId);
                return null;
            }
            return frenchName;
        } catch (IOException e) {
            logger.error("Erreur lors du scraping pour la carte ID {} : {}", cardId, e.getMessage());
            return null;
        }
    }

    private String getFrenchTranslationForSet(String setCode) {
        // À implémenter si nécessaire
        return null;
    }
}