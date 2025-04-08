package com.example.demo.service;

import com.example.demo.entity.YugiohSet;
import com.example.demo.repository.YugiohSetRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class YugiohDataService {

    private static final Logger logger = LoggerFactory.getLogger(YugiohDataService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private YugiohCardService yugiohCardService;

    @Autowired
    private YugiohSetService yugiohSetService;

    @Autowired
    private YugiohSetRepository yugiohSetRepository;

    // Liste statique pour fallback des sets
    private static final Map<String, String> STATIC_SETS = new HashMap<>();
    static {
        STATIC_SETS.put("LOB", "Legend of Blue Eyes White Dragon");
        STATIC_SETS.put("MRD", "Metal Raiders");
        STATIC_SETS.put("SRL", "Spell Ruler");
        STATIC_SETS.put("PSV", "Pharaoh's Servant");
        STATIC_SETS.put("LON", "Labyrinth of Nightmare");
    }

    // Liste statique pour les cartes du set "LOB"
    private static final List<Map<String, Object>> STATIC_CARDS_LOB = new ArrayList<>();
    static {
        // Simuler quelques cartes pour "LOB"
        Map<String, Object> card1 = new HashMap<>();
        card1.put("id", "LOB-001");
        card1.put("name", "Blue-Eyes White Dragon");
        card1.put("desc", "This legendary dragon is a powerful engine of destruction.");
        card1.put("image_url", "/images/Yugioh/LOB-001.jpg");
        card1.put("atk", 3000);
        card1.put("def", 2500);
        card1.put("type", "Dragon");
        STATIC_CARDS_LOB.add(card1);

        Map<String, Object> card2 = new HashMap<>();
        card2.put("id", "LOB-002");
        card2.put("name", "Dark Magician");
        card2.put("desc", "The ultimate wizard in terms of attack and defense.");
        card2.put("image_url", "/images/Yugioh/LOB-002.jpg");
        card2.put("atk", 2500);
        card2.put("def", 2100);
        card2.put("type", "Spellcaster");
        STATIC_CARDS_LOB.add(card2);
    }

    @Transactional
    public void processYugiohSet(String setCode) {
        logger.info("Début du traitement de la série Yu-Gi-Oh : {}", setCode);

        // Vérifier si le set existe déjà
        Optional<YugiohSet> existingSetOpt = yugiohSetRepository.findBySetCode(setCode);
        if (existingSetOpt.isPresent()) {
            logger.info("La série Yu-Gi-Oh {} existe déjà, pas de traitement nécessaire", setCode);
            return;
        }

        // Construire l'URL pour l'API YGOPRODECK
        String setName = getSetName(setCode);
        if (setName == null) {
            logger.error("Nom de la série introuvable pour le code : {}", setCode);
            throw new IllegalArgumentException("Série Yu-Gi-Oh inconnue : " + setCode);
        }

        List<Map<String, Object>> cardsData;
        if (setCode.equalsIgnoreCase("LOB")) {
            logger.info("Utilisation des cartes statiques pour le set LOB");
            cardsData = STATIC_CARDS_LOB;
        } else {
            String url = "https://ygoprodeck.com/api/v8/cardinfo.php?cardset=" + setName;
            logger.info("URL construite avant envoi : {}", url);

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

                if (!response.getStatusCode().is2xxSuccessful()) {
                    logger.error("Erreur lors de la récupération des cartes pour la série {} : {}", setCode, response.getStatusCode());
                    throw new RuntimeException("Échec de la récupération des données pour la série : " + setCode);
                }

                String responseBody = response.getBody();
                if (responseBody == null) {
                    logger.error("Aucune réponse de l'API pour la série Yu-Gi-Oh : {}", setCode);
                    throw new RuntimeException("Échec de la récupération des données pour la série : " + setCode);
                }

                JsonNode root = objectMapper.readTree(responseBody);
                cardsData = new ArrayList<>();
                for (JsonNode cardNode : root.path("data")) {
                    Map<String, Object> card = new HashMap<>();
                    card.put("id", cardNode.path("id").asText());
                    card.put("name", cardNode.path("name").asText());
                    card.put("desc", cardNode.path("desc").asText());
                    card.put("image_url", cardNode.path("card_images").get(0).path("image_url").asText());
                    card.put("atk", cardNode.path("atk").isMissingNode() ? null : cardNode.path("atk").asInt());
                    card.put("def", cardNode.path("def").isMissingNode() ? null : cardNode.path("def").asInt());
                    card.put("type", cardNode.path("type").asText());
                    cardsData.add(card);
                }
            } catch (Exception e) {
                logger.error("Erreur lors du traitement de la série Yu-Gi-Oh {} : {}", setCode, e.getMessage());
                throw new RuntimeException("Erreur lors du traitement de la série : " + setCode, e);
            }
        }

        // Sauvegarder le set
        int totalCards = cardsData.size();
        Long setId = yugiohSetService.saveSet(setCode, totalCards);
        yugiohSetService.saveSetTranslation(setId, "en", setName);

        // Traiter chaque carte
        for (Map<String, Object> cardData : cardsData) {
            String cardNumber = (String) cardData.get("id");
            String nameEn = (String) cardData.get("name");
            String descEn = (String) cardData.get("desc");
            String imagePath = (String) cardData.get("image_url");
            Integer attack = (Integer) cardData.get("atk");
            Integer defense = (Integer) cardData.get("def");
            String type = (String) cardData.get("type");

            Long cardId = yugiohCardService.saveYugiohCard(cardNumber, "Yugioh", attack, defense, type);
            yugiohCardService.saveCardTranslation(cardId, "en", nameEn, descEn);
            logger.info("Carte Yu-Gi-Oh sauvegardée : {} dans le set {} (ID: {})", cardNumber, setCode, cardId);
        }
    }

    private String getSetName(String setCode) {
        // Vérifier d'abord dans la liste statique
        if (STATIC_SETS.containsKey(setCode.toUpperCase())) {
            logger.info("Utilisation du nom statique pour le set {} : {}", setCode, STATIC_SETS.get(setCode.toUpperCase()));
            return STATIC_SETS.get(setCode.toUpperCase());
        }

        try {
            String url = "https://ygoprodeck.com/api/v8/cardsets";
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Erreur lors de la récupération des sets : {}", response.getStatusCode());
                return null;
            }

            String responseBody = response.getBody();
            if (responseBody == null) {
                logger.error("Aucune réponse de l'API YGOPRODECK pour la liste des sets");
                return null;
            }

            JsonNode sets = objectMapper.readTree(responseBody);
            for (JsonNode setNode : sets) {
                if (setNode.path("set_code").asText().equalsIgnoreCase(setCode)) {
                    return setNode.path("set_name").asText();
                }
            }
            logger.warn("Set Yu-Gi-Oh non trouvé pour le code : {}", setCode);
            return null;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du nom de la série {} : {}", setCode, e.getMessage());
            return null;
        }
    }
}