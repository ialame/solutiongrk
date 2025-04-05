package com.example.demo.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class TcgdexApiClient {
    private static final Logger logger = LoggerFactory.getLogger(TcgdexApiClient.class);
    private final WebClient webClient;

    public TcgdexApiClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.tcgdex.net/v2")
                .build();
    }

    public JsonNode fetchSets(String language) {
        try {
            return webClient.get()
                    .uri("/{lang}/sets", language)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .doOnSuccess(json -> logger.debug("Sets récupérés pour {} : {}", language, json))
                    .doOnError(e -> logger.error("Erreur lors de la récupération des sets pour {} : {}", language, e.getMessage()))
                    .block();
        } catch (WebClientResponseException e) {
            logger.warn("Sets non disponibles pour la langue {} : {}", language, e.getMessage());
            return null;
        }
    }

    public JsonNode fetchCards(String setId, String language) {
        try {
            return webClient.get()
                    .uri("/{lang}/sets/{setId}", language, setId)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .doOnSuccess(json -> logger.debug("Cartes récupérées pour set {} en {} : {}", setId, language, json))
                    .doOnError(e -> logger.error("Erreur lors de la récupération des cartes pour set {} en {} : {}", setId, language, e.getMessage()))
                    .block();
        } catch (WebClientResponseException e) {
            logger.warn("Set {} non disponible en {} : {}", setId, language, e.getMessage());
            return null;
        }
    }
}