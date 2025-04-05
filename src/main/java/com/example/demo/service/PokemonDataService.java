package com.example.demo.service;

import com.example.demo.client.TcgdexApiClient;
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
    private TcgdexApiClient apiClient;

    @Autowired
    private EntityPersistenceService persistenceService;

    @Autowired
    private ImageDownloader imageDownloader;

    private static final Language[] SUPPORTED_LANGUAGES = {Language.EN, Language.FR, Language.IT};

    @Override
    public void initializeData() throws IOException {
        logger.info("Initializing Pokémon data with TCGdex...");
        JsonNode setsNode = apiClient.fetchSets("en");
        if (setsNode != null) {
            logger.info("Nombre de sets récupérés : {}", setsNode.size());
            for (JsonNode setNode : setsNode) {
                try {
                    processSet(setNode);
                } catch (Exception e) {
                    logger.error("Erreur lors du traitement du set {} : {}", setNode.get("id").asText(), e.getMessage(), e);
                }
            }
        } else {
            logger.warn("Aucun set récupéré depuis TCGdex.");
        }
        logger.info("Pokémon data initialization completed.");
    }

    private void processSet(JsonNode setNode) throws IOException {
        String setId = setNode.get("id").asText();
        String setCode = setNode.get("id").asText();
        String serieName = setNode.has("serie") && setNode.get("serie").has("name") ? setNode.get("serie").get("name").asText() : "Unknown Series";
        String gameType = "Pokemon";
        String ptcgoCode = setNode.has("tcgOnline") ? setNode.get("tcgOnline").asText() : null;
        String releaseDate = setNode.has("releaseDate") ? setNode.get("releaseDate").asText() : null;
        Integer totalCards = setNode.has("cardCount") && setNode.get("cardCount").has("total") ? setNode.get("cardCount").get("total").asInt() : null;
        String legalities = setNode.has("legal") ? setNode.get("legal").toString() : null;

        Serie serie = persistenceService.saveSerie(serieName, gameType);

        for (Language lang : SUPPORTED_LANGUAGES) {
            String langCode = lang.getCode();
            JsonNode setLangNode = apiClient.fetchCards(setId, langCode);
            if (setLangNode != null && setLangNode.has("name")) {
                String setName = setLangNode.get("name").asText();
                CardSet set = persistenceService.saveSet(setCode, setName, serie, ptcgoCode, releaseDate, totalCards, legalities);
                processCards(setLangNode.get("cards"), set, lang);
            } else {
                logger.warn("Set {} non disponible ou mal formé pour la langue {}", setId, langCode);
            }
        }
    }

    private void processCards(JsonNode cardsNode, CardSet set, Language language) {
        if (cardsNode == null || !cardsNode.isArray()) {
            logger.warn("Aucune carte trouvée pour le set {} en {}", set.getSetCode(), language);
            return;
        }
        logger.info("Nombre de cartes récupérées pour le set {} en {} : {}", set.getSetCode(), language, cardsNode.size());
        for (JsonNode cardNode : cardsNode) {
            try {
                processCard(cardNode, set, language);
            } catch (Exception e) {
                logger.error("Erreur lors du traitement de la carte {} dans le set {} : {}", cardNode.has("id") ? cardNode.get("id").asText() : "inconnu", set.getSetCode(), e.getMessage(), e);
            }
        }
    }

    private void processCard(JsonNode cardNode, CardSet set, Language language) throws IOException {
        logger.debug("Traitement de la carte : {}", cardNode.get("id").asText());
        PokemonCard card = new PokemonCard();
        card.setCardNumber(cardNode.get("localId").asText());
        card.setRarity(cardNode.has("rarity") ? cardNode.get("rarity").asText() : "Unknown");
        card.setGameType("Pokemon");

        String imageUrl = cardNode.has("image") ? cardNode.get("image").asText() + "/high.png" : null;
        if (imageUrl != null) {
            try {
                String imagePath = imageDownloader.downloadImage(imageUrl, "Pokemon", cardNode.get("id").asText());
                card.setImagePath(imagePath);
            } catch (IOException e) {
                logger.error("Erreur lors du téléchargement de l'image pour la carte {} : {}", cardNode.get("id").asText(), e.getMessage());
                card.setImagePath(null);
            }
        }

        if (cardNode.has("category") && "Pokemon".equals(cardNode.get("category").asText())) {
            if (cardNode.has("hp")) card.setHp(cardNode.get("hp").asInt());
            if (cardNode.has("types") && cardNode.get("types").size() > 0) card.setEnergyType(cardNode.get("types").get(0).asText());
            if (cardNode.has("weaknesses") && cardNode.get("weaknesses").size() > 0) card.setWeakness(cardNode.get("weaknesses").get(0).get("type").asText());
        }

        String description = null;
        String flavorText = cardNode.has("description") ? cardNode.get("description").asText() : null;
        if (cardNode.has("types") && cardNode.has("hp")) {
            description = cardNode.get("types").get(0).asText() + "-type Pokémon with " + cardNode.get("hp").asText() + " HP";
        } else if (cardNode.has("attacks") && cardNode.get("attacks").size() > 0) {
            JsonNode attack = cardNode.get("attacks").get(0);
            description = attack.get("name").asText() + ": " + (attack.has("text") ? attack.get("text").asText() : attack.get("damage").asText());
        }

        card.addTranslation(new PokemonCardTranslation(card, language, cardNode.get("name").asText(), description, flavorText));

        logger.debug("Appel à saveCard pour la carte : {}", card.getCardNumber());
        persistenceService.saveCard(card, set);
        logger.debug("Carte {} traitée avec succès", card.getCardNumber());
    }
}