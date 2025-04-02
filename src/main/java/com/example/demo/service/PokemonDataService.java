package com.example.demo.service;

import com.example.demo.client.PokemonApiClient;
import com.example.demo.entity.*;
import com.example.demo.image.ImageDownloader;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class PokemonDataService {

    private static final Logger logger = LoggerFactory.getLogger(PokemonDataService.class);

    @Autowired
    private PokemonApiClient apiClient;

    @Autowired
    private EntityPersistenceService persistenceService;

    @Autowired
    private ImageDownloader imageDownloader;

    public void initializePokemonData() throws IOException {
        logger.info("Initializing Pokémon data...");
        JsonNode setsNode = apiClient.fetchSets();
        if (setsNode != null && setsNode.has("data")) {
            logger.info("Nombre de sets récupérés : {}", setsNode.get("data").size());
            for (JsonNode setNode : setsNode.get("data")) {
                try {
                    processSet(setNode);
                } catch (Exception e) {
                    logger.error("Erreur lors du traitement du set {} : {}", setNode.get("id").asText(), e.getMessage(), e);
                }
            }
        }
        logger.info("Pokémon data initialization completed.");
    }

    @Transactional
    public void testPersistence() {
        logger.info("Testing persistence...");
        Serie testSerie = new Serie();
        testSerie.setGameType("Pokemon");
        testSerie.addTranslation(new SerieTranslation(testSerie, Language.US, "Test Serie"));
        persistenceService.saveSerie("Test Serie", "Pokemon"); // Utilise le service factorisé
        logger.info("Test Serie sauvegardée.");
    }

    private void processSet(JsonNode setNode) throws IOException {
        String setId = setNode.get("id").asText();
        String setCode = setNode.get("id").asText();
        String serieName = setNode.get("series").asText();
        String setName = setNode.get("name").asText();
        String gameType = "Pokemon";

        Serie serie = persistenceService.saveSerie(serieName, gameType);
        Set set = persistenceService.saveSet(setCode, setName, serie);

        int page = 1;
        int pageSize = 50;
        while (true) {
            JsonNode cardsNode = apiClient.fetchCards(setId, page, pageSize);
            if (cardsNode == null || !cardsNode.has("data") || cardsNode.get("data").size() == 0) {
                logger.info("Aucune carte supplémentaire trouvée pour le set {} à la page {}", setId, page);
                break;
            }
            logger.info("Nombre de cartes récupérées pour le set {} à la page {} : {}", setId, page, cardsNode.get("data").size());
            for (JsonNode cardNode : cardsNode.get("data")) {
                try {
                    processCard(cardNode, set, gameType);
                } catch (Exception e) {
                    logger.error("Erreur lors du traitement de la carte {} dans le set {} : {}", cardNode.get("number").asText(), setId, e.getMessage(), e);
                }
            }
            page++;
        }
    }

    private void processCard(JsonNode cardNode, Set set, String gameType) throws IOException {
        String cardId = cardNode.get("id").asText();
        String cardNumber = cardNode.get("number").asText();
        String imageUrl = cardNode.get("images").get("large").asText();
        String rarity = cardNode.get("rarity") != null ? cardNode.get("rarity").asText() : "Unknown";

        PokemonCard newCard = new PokemonCard();
        newCard.setCardNumber(cardNumber);
        try {
            String imagePath = imageDownloader.downloadImage(imageUrl, gameType, cardId);
            newCard.setImagePath(imagePath);
        } catch (IOException e) {
            logger.error("Erreur lors du téléchargement de l'image pour la carte {} : {}", cardId, e.getMessage());
            newCard.setImagePath(null);
        }
        newCard.setRarity(rarity);
        if (cardNode.has("types")) {
            newCard.setEnergyType(cardNode.get("types").get(0).asText());
        }
        if (cardNode.has("hp")) {
            newCard.setHp(cardNode.get("hp").asInt());
        }
        if (cardNode.has("weaknesses")) {
            newCard.setWeakness(cardNode.get("weaknesses").get(0).get("type").asText());
        }
        String cardName = cardNode.get("name").asText();
        newCard.addTranslation(new CardTranslation(newCard, Language.US, cardName, null));

        persistenceService.saveCard(newCard, set);
    }
}