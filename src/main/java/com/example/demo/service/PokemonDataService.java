package com.example.demo.service;

import com.example.demo.client.PokemonApiClient;
import com.example.demo.entity.*;
import com.example.demo.image.ImageDownloader;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PokemonDataService implements GameDataService {

    private static final Logger logger = LoggerFactory.getLogger(PokemonDataService.class);

    @Autowired
    private PokemonApiClient apiClient;

    @Autowired
    private EntityPersistenceService persistenceService;

    @Autowired
    private ImageDownloader imageDownloader;

    @Override
    public void initializeData() throws IOException {
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

    private void processSet(JsonNode setNode) throws IOException {
        String setId = setNode.get("id").asText();
        String setCode = setNode.get("id").asText();
        String serieName = setNode.get("series").asText();
        String setName = setNode.get("name").asText();
        String gameType = "Pokemon";
        String ptcgoCode = setNode.has("ptcgoCode") ? setNode.get("ptcgoCode").asText() : null;
        String releaseDate = setNode.has("releaseDate") ? setNode.get("releaseDate").asText() : null;
        Integer totalCards = setNode.has("total") ? setNode.get("total").asInt() : null;

        Serie serie = persistenceService.saveSerie(serieName, gameType);
        CardSet set = persistenceService.saveSet(setCode, setName, serie, ptcgoCode, releaseDate, totalCards);

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
                    processCard(cardNode, set);
                } catch (Exception e) {
                    logger.error("Erreur lors du traitement de la carte {} dans le set {} : {}", cardNode.get("number").asText(), setId, e.getMessage(), e);
                }
            }
            page++;
        }
    }

    private void processCard(JsonNode cardNode, CardSet set) throws IOException {
        logger.debug("Traitement de la carte : {}", cardNode.get("number").asText());
        PokemonCard card = new PokemonCard();
        card.setCardNumber(cardNode.get("number").asText());
        card.setRarity(cardNode.get("rarity") != null ? cardNode.get("rarity").asText() : "Unknown");
        try {
            String imagePath = imageDownloader.downloadImage(cardNode.get("images").get("large").asText(), "Pokemon", cardNode.get("id").asText());
            card.setImagePath(imagePath);
        } catch (IOException e) {
            logger.error("Erreur lors du téléchargement de l'image pour la carte {} : {}", cardNode.get("id").asText(), e.getMessage());
            card.setImagePath(null);
        }
        if (cardNode.has("types")) card.setEnergyType(cardNode.get("types").get(0).asText());
        if (cardNode.has("hp")) card.setHp(cardNode.get("hp").asInt());
        if (cardNode.has("weaknesses")) card.setWeakness(cardNode.get("weaknesses").get(0).get("type").asText());
        card.addTranslation(new CardTranslation(card, Language.US, cardNode.get("name").asText(), null));

        logger.debug("Appel à saveCard pour la carte : {}", card.getCardNumber());
        persistenceService.saveCard(card, set);
        logger.debug("Carte {} traitée avec succès", card.getCardNumber());
    }
}