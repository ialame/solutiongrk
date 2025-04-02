package com.example.demo.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PokemonApiClient {

    private static final Logger logger = LoggerFactory.getLogger(PokemonApiClient.class);
    private final WebClient webClient;

    @Autowired
    public PokemonApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.pokemontcg.io/v2").build();
    }

    public JsonNode fetchSets() {
        String url = "/sets";
        logger.info("Fetching Pokémon sets from: {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    public JsonNode fetchCards(String setId, int page, int pageSize) {
        String url = String.format("/cards?q=set.id:%s&page=%d&pageSize=%d", setId, page, pageSize);
        logger.info("Fetching cards from: {}", url);
        try {
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
        } catch (Exception ex) {
            logger.warn("No more cards found for set {} at page {}: {}", setId, page, ex.getMessage());
            return null;
        }
    }
}
