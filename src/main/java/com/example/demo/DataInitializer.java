package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.SerieRepository;
import com.example.demo.repository.SetRepository;
import com.example.demo.service.CardService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardService cardService;

    @Override
    public void run(String... args) throws Exception {
        initializePokemonData();
        initializeYuGiOhData();
    }

    public void initializePokemonData() throws Exception {
        WebClient webClient = webClientBuilder.baseUrl("https://api.pokemontcg.io/v2").build();
        JsonNode setsNode = webClient.get()
                .uri("/sets")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block()
                .get("data");

        for (JsonNode setNode : setsNode) {
            String setId = setNode.get("id").asText();
            String setName = setNode.get("name").asText();
            String setCode = setNode.get("series").asText() + "-" + setId;

            String serieName = setNode.get("series").asText();
            Serie serie = serieRepository.findByTranslations_LanguageAndTranslations_Name(Language.US, serieName)
                    .orElseGet(() -> {
                        Serie newSerie = new Serie();
                        newSerie.setGameType("Pokemon");
                        newSerie.addTranslation(Language.US, serieName);
                        newSerie.addTranslation(Language.FR, serieName);
                        return serieRepository.save(newSerie);
                    });

            Set set = setRepository.findBySetCode(setCode)
                    .orElseGet(() -> {
                        Set newSet = new Set();
                        newSet.setSetCode(setCode);
                        newSet.setGameType("Pokemon");
                        newSet.setSerie(serie);
                        newSet.setTranslations(List.of(
                                new SetTranslation(newSet, Language.US, setName),
                                new SetTranslation(newSet, Language.FR, setName)
                        ));
                        return setRepository.save(newSet);
                    });

            int page = 1;
            int pageSize = 50;
            boolean hasMoreCards = true;

            while (hasMoreCards) {
                JsonNode cardsResponse = fetchPokemonCards(webClient, setId, page, pageSize);
                JsonNode cardsNode = cardsResponse.get("data");
                int totalCount = cardsResponse.get("totalCount").asInt();
                int currentCount = page * pageSize;

                for (JsonNode cardNode : cardsNode) {
                    String cardNumber = cardNode.get("number") != null ? cardNode.get("number").asText() : "Unknown";
                    if (cardRepository.findByCardNumberAndSets_SetCode(cardNumber, setCode).isEmpty()) {
                        PokemonCard card = new PokemonCard();
                        card.setCardNumber(cardNumber);
                        card.setRarity(cardNode.get("rarity") != null ? cardNode.get("rarity").asText() : "Unknown");
                        card.setEnergyType(cardNode.get("types") != null && cardNode.get("types").size() > 0 ? cardNode.get("types").get(0).asText() : "Inconnu");
                        card.setHp(cardNode.get("hp") != null ? Integer.parseInt(cardNode.get("hp").asText()) : 0);
                        card.setWeakness(cardNode.get("weaknesses") != null && cardNode.get("weaknesses").size() > 0 ? cardNode.get("weaknesses").get(0).get("type").asText() : null);
                        card.setTranslations(List.of(
                                new CardTranslation(card, Language.US,
                                        cardNode.get("name") != null ? cardNode.get("name").asText() : "Unknown",
                                        cardNode.get("flavorText") != null ? cardNode.get("flavorText").asText() : "")
                        ));
                        card.setSets(Collections.singletonList(set));

                        String imageUrl = cardNode.get("images") != null && cardNode.get("images").get("large") != null
                                ? cardNode.get("images").get("large").asText()
                                : "https://via.placeholder.com/150";
                        String imagePath = downloadImage(imageUrl, "pokemon", cardNode.get("id").asText());
                        card.setImagePath(imagePath);

                        cardService.saveCard(card);
                        System.out.println("Carte Pokémon ajoutée : " + card.getCardNumber());
                    }
                }

                hasMoreCards = currentCount < totalCount;
                page++;
            }
        }
    }
    public void initializeYuGiOhData() throws Exception {
        WebClient webClient = webClientBuilder.baseUrl("https://db.ygoprodeck.com/api/v7").build();
        JsonNode setsNode = webClient.get()
                .uri("/cardsets")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        for (JsonNode setNode : setsNode) {
            String setCode = setNode.get("set_code").asText();
            String setName = setNode.get("set_name").asText();
            String serieName = extractSerieName(setName);

            Serie serie = serieRepository.findByTranslations_LanguageAndTranslations_Name(Language.US, serieName)
                    .orElseGet(() -> {
                        Serie newSerie = new Serie();
                        newSerie.setGameType("YuGiOh");
                        newSerie.addTranslation(Language.US, serieName);
                        newSerie.addTranslation(Language.FR, translateSerieName(serieName, Language.FR));
                        return serieRepository.save(newSerie);
                    });

            Set set = setRepository.findBySetCode(setCode)
                    .orElseGet(() -> {
                        Set newSet = new Set();
                        newSet.setSetCode(setCode);
                        newSet.setGameType("YuGiOh");
                        newSet.setSerie(serie);
                        newSet.setTranslations(List.of(
                                new SetTranslation(newSet, Language.US, setName),
                                new SetTranslation(newSet, Language.FR, translateSetName(setName, Language.FR))
                        ));
                        return setRepository.save(newSet);
                    });

            JsonNode cardsNode = fetchYuGiOhCards(webClient, setCode);
            for (JsonNode cardNode : cardsNode) {
                String cardNumber = cardNode.get("id").asText();
                String cardName = cardNode.get("name").asText();

                if (cardRepository.findByCardNumberAndSets_SetCode(cardNumber, setCode).isEmpty()) {
                    YuGiOhCard card = new YuGiOhCard();
                    card.setCardNumber(cardNumber);
                    card.setAttack(cardNode.get("atk") != null ? cardNode.get("atk").asInt() : 0);
                    card.setDefense(cardNode.get("def") != null ? cardNode.get("def").asInt() : 0);
                    card.setLevel(cardNode.get("level") != null ? cardNode.get("level").asInt() : 0);
                    card.setCardType(cardNode.get("type") != null ? cardNode.get("type").asText() : "Unknown");
                    card.setTranslations(List.of(
                            new CardTranslation(card, Language.US, cardName, cardNode.get("desc").asText())
                    ));
                    card.setSets(Collections.singletonList(set));

                    String imageUrl = cardNode.get("card_images") != null && cardNode.get("card_images").get(0) != null
                            ? cardNode.get("card_images").get(0).get("image_url").asText()
                            : "https://via.placeholder.com/150";
                    String imagePath = downloadImage(imageUrl, "yugioh", cardNumber);
                    card.setImagePath(imagePath);

                    cardService.saveCard(card);
                    System.out.println("Carte Yu-Gi-Oh! ajoutée : " + card.getCardNumber());
                }
            }
        }
    }

    private JsonNode fetchPokemonCards(WebClient webClient, String setId, int page, int pageSize) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cards")
                        .queryParam("q", "set.id:" + setId)
                        .queryParam("page", page)
                        .queryParam("pageSize", pageSize)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    private JsonNode fetchYuGiOhCards(WebClient webClient, String setCode) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cardinfo.php")
                        .queryParam("cardset", setCode)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block()
                .get("data");
    }

    private String extractSerieName(String setName) {
        if (setName.contains("Blue Eyes") || setName.contains("Metal Raiders")) {
            return "Early Sets";
        }
        return setName.split(" ")[0];
    }

    private String translateSerieName(String englishName, Language language) {
        if (language == Language.FR) {
            return switch (englishName) {
                case "Early Sets" -> "Sets Initiaux";
                case "Phantom Darkness" -> "Ténèbres Fantômes";
                default -> englishName;
            };
        }
        return englishName;
    }

    private String translateSetName(String englishName, Language language) {
        if (language == Language.FR) {
            return switch (englishName) {
                case "Legend of Blue Eyes White Dragon" -> "Légende du Dragon Blanc aux Yeux Bleus";
                case "Metal Raiders" -> "Les Pillards de Métal";
                default -> englishName;
            };
        }
        return englishName;
    }

    private String downloadImage(String imageUrl, String gameType, String cardId) {
        // Implémentation existante pour télécharger l'image
        return "path/to/" + gameType + "/" + cardId + ".jpg";
    }
}