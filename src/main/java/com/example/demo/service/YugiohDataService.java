package com.example.demo.service;

import com.example.demo.entity.YugiohSet;
import com.example.demo.repository.YugiohSetRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

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

        String url = "https://db.ygoprodeck.com/api/v7/cardinfo.php?cardset=" + setName;
        logger.info("URL construite avant envoi : {}", url);

        try {
            String response = restTemplate.getForObject(url, String.class);
            if (response == null) {
                logger.error("Aucune réponse de l'API pour la série Yu-Gi-Oh : {}", setCode);
                throw new RuntimeException("Échec de la récupération des données pour la série : " + setCode);
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode cards = root.path("data");

            // Sauvegarder le set
            int totalCards = cards.size();
            Long setId = yugiohSetService.saveSet(setCode, totalCards); // Ligne 69
            yugiohSetService.saveSetTranslation(setId, "en", setName);

            // Traiter chaque carte
            for (JsonNode cardNode : cards) {
                String cardNumber = cardNode.path("id").asText();
                String nameEn = cardNode.path("name").asText();
                String descEn = cardNode.path("desc").asText();
                String imagePath = cardNode.path("card_images").get(0).path("image_url").asText();
                Integer attack = cardNode.path("atk").isMissingNode() ? null : cardNode.path("atk").asInt();
                Integer defense = cardNode.path("def").isMissingNode() ? null : cardNode.path("def").asInt();
                String type = cardNode.path("type").asText();

                Long cardId = yugiohCardService.saveYugiohCard(cardNumber, "Yugioh", attack, defense, type);
                yugiohCardService.saveCardTranslation(cardId, "en", nameEn, descEn);
                logger.info("Carte Yu-Gi-Oh sauvegardée : {} dans le set {} (ID: {})", cardNumber, setCode, cardId);
            }
        } catch (Exception e) {
            logger.error("Erreur lors du traitement de la série Yu-Gi-Oh {} : {}", setCode, e.getMessage());
            throw new RuntimeException("Erreur lors du traitement de la série : " + setCode, e);
        }
    }

    private String getSetName(String setCode) {
        try {
            String url = "https://db.ygoprodeck.com/api/v7/cardsets/" + setCode;
            String response = restTemplate.getForObject(url, String.class);
            if (response == null) {
                return null;
            }
            JsonNode setNode = objectMapper.readTree(response);
            return setNode.path("set_name").asText();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du nom de la série {} : {}", setCode, e.getMessage());
            return null;
        }
    }
}