package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.SerieRepository;
import com.example.demo.repository.SetRepository;
import com.example.demo.service.CardService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardService cardService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String IMAGE_DIR = "src/main/resources/static/images/";

    @Override
    public void run(String... args) throws Exception {
        // Créer le dossier pour les images s’il n’existe pas
        Files.createDirectories(Paths.get(IMAGE_DIR));

        // Initialiser les données Pokémon
        initializePokemonData();

        // Initialiser les données Yu-Gi-Oh!
        initializeYuGiOhData();
    }

    private void initializePokemonData() throws Exception {
        WebClient webClient = webClientBuilder.baseUrl("https://api.pokemontcg.io/v2").build();

        // Récupérer les sets
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

            // Créer ou récupérer une Serie
            String serieName = setNode.get("series").asText();
            Serie serie = serieRepository.findByNameAndGameType(serieName, "Pokemon")
                    .orElseGet(() -> {
                        Serie newSerie = new Serie();
                        newSerie.setName(serieName);
                        newSerie.setGameType("Pokemon");
                        return serieRepository.save(newSerie);
                    });

            // Créer ou récupérer un Set
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

            // Pagination des cartes
            int page = 1;
            int pageSize = 50; // Nombre de cartes par page
            boolean hasMoreCards = true;

            while (hasMoreCards) {
                final int currentPage = page; // Copie locale "effectively final"
                JsonNode cardsResponse = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/cards")
                                .queryParam("q", "set.id:" + setId)
                                .queryParam("page", currentPage)
                                .queryParam("pageSize", pageSize)
                                .build())
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .block();

                JsonNode cardsNode = cardsResponse.get("data");
                int totalCount = cardsResponse.get("totalCount").asInt();
                int currentCount = page * pageSize;

                for (JsonNode cardNode : cardsNode) {
                    String cardNumber = cardNode.get("number") != null ? cardNode.get("number").asText() : "Unknown";

                    // Vérifier si la carte existe déjà
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

                        // Télécharger l’image
                        String imageUrl = cardNode.get("images") != null && cardNode.get("images").get("large") != null
                                ? cardNode.get("images").get("large").asText()
                                : "https://via.placeholder.com/150";
                        String imagePath = downloadImage(imageUrl, "pokemon", cardNode.get("id").asText());
                        card.setImagePath(imagePath);

                        cardService.saveCard(card);
                        System.out.println("Carte Pokémon ajoutée : " + card.getCardNumber());
                    }
                }

                // Vérifier s’il y a plus de pages
                hasMoreCards = currentCount < totalCount;
                page++;
            }
        }
    }

    private void initializeYuGiOhData() throws Exception {
        WebClient webClient = webClientBuilder.baseUrl("https://db.ygoprodeck.com/api/v7").build();

        // Récupérer les sets
        JsonNode setsNode = webClient.get()
                .uri("/cardsets")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        for (JsonNode setNode : setsNode) {
            String setCode = setNode.get("set_code").asText();
            String setName = setNode.get("set_name").asText();

            // Créer ou récupérer une Serie
            String serieName = setName.split(" - ")[0];
            Serie serie = serieRepository.findByNameAndGameType(serieName, "YuGiOh")
                    .orElseGet(() -> {
                        Serie newSerie = new Serie();
                        newSerie.setName(serieName);
                        newSerie.setGameType("YuGiOh");
                        return serieRepository.save(newSerie);
                    });

            // Créer ou récupérer un Set
            Set set = setRepository.findBySetCode(setCode)
                    .orElseGet(() -> {
                        Set newSet = new Set();
                        newSet.setSetCode(setCode);
                        newSet.setGameType("YuGiOh");
                        newSet.setSerie(serie);
                        newSet.setTranslations(List.of(
                                new SetTranslation(newSet, Language.US, setName),
                                new SetTranslation(newSet, Language.FR, setName) // Placeholder
                        ));
                        return setRepository.save(newSet);
                    });

            // Récupérer les cartes du set
            JsonNode cardsNode = webClient.get()
                    .uri("/cardinfo.php?cardset={setName}", setName)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block()
                    .get("data");

            for (JsonNode cardNode : cardsNode) {
                String cardNumber = cardNode.get("id").asText();

                // Vérifier si la carte existe déjà
                if (cardRepository.findByCardNumberAndSets_SetCode(cardNumber, setCode).isEmpty()) {
                    YuGiOhCard card = new YuGiOhCard();
                    card.setCardNumber(cardNumber);
                    card.setRarity(cardNode.get("card_sets") != null ? cardNode.get("card_sets").get(0).get("set_rarity").asText() : "Common");
                    card.setLevel(cardNode.get("level") != null ? cardNode.get("level").asInt() : 0);
                    card.setAttack(cardNode.get("atk") != null ? cardNode.get("atk").asInt() : 0);
                    card.setDefense(cardNode.get("def") != null ? cardNode.get("def").asInt() : 0);
                    card.setCardType(cardNode.get("type").asText());
                    card.setTranslations(List.of(
                            new CardTranslation(card, Language.US, cardNode.get("name").asText(), cardNode.get("desc").asText())
                    ));
                    card.setSets(Collections.singletonList(set));

                    // Télécharger l’image
                    String imageUrl = cardNode.get("card_images").get(0).get("image_url").asText();
                    String imagePath = downloadImage(imageUrl, "yugioh", cardNode.get("id").asText());
                    card.setImagePath(imagePath);

                    cardService.saveCard(card);
                    System.out.println("Carte Yu-Gi-Oh! ajoutée : " + card.getCardNumber());
                }
            }
        }
    }

    private String downloadImage(String imageUrl, String gameType, String cardId) throws Exception {
        String fileName = gameType + "_" + cardId + ".png";
        Path filePath = Paths.get(IMAGE_DIR, fileName);

        // Vérifier si l’image existe déjà
        if (!Files.exists(filePath)) {
            Resource resource = new UrlResource(imageUrl);
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                fos.write(resource.getInputStream().readAllBytes());
            }
            System.out.println("Image téléchargée : " + filePath);
        }
        return "/images/" + fileName;
    }
}
