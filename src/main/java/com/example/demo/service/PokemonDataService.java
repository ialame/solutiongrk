package com.example.demo.service;

import com.example.demo.entity.PokemonSet;
import com.example.demo.repository.PokemonSetRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class PokemonDataService {

    private static final Logger logger = LoggerFactory.getLogger(PokemonDataService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PokemonCardService pokemonCardService;

    @Autowired
    private PokemonSetService pokemonSetService;

    @Autowired
    private PokemonSetRepository pokemonSetRepository;

    @Transactional
    public void processPokemonSet(String setCode) {
        logger.info("Début du traitement de la série Pokémon : {}", setCode);

        // Vérifier si le set existe déjà
        Optional<PokemonSet> existingSetOpt = pokemonSetRepository.findBySetCode(setCode);
        if (existingSetOpt.isPresent()) {
            logger.info("La série Pokémon {} existe déjà, pas de traitement nécessaire", setCode);
            return;
        }

        // Appeler l'API Pokémon TCG
        String url = "https://api.pokemontcg.io/v2/cards?q=set.id:" + setCode;
        logger.info("URL construite avant envoi : {}", url);

        try {
            String response = restTemplate.getForObject(url, String.class);
            if (response == null) {
                logger.error("Aucune réponse de l'API pour la série Pokémon : {}", setCode);
                return;
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode cards = root.path("data");

            // Sauvegarder le set
            int totalCards = cards.size();
            Long setId = pokemonSetService.saveSet(setCode, totalCards).getId();
            pokemonSetService.saveSetTranslation(setId, "en", setCode);

            // Traiter chaque carte
            for (JsonNode cardNode : cards) {
                String cardId = cardNode.path("id").asText();
                String nameEn = cardNode.path("name").asText();
                String flavorTextEn = cardNode.path("flavorText").asText("");
                String imageUrl = cardNode.path("images").path("small").asText();
                Integer hp = cardNode.path("hp").asText("Unknown").equals("Unknown") ? null : Integer.parseInt(cardNode.path("hp").asText());
                String type = cardNode.path("supertype").asText();
                String energyType = cardNode.path("types").isArray() ? cardNode.path("types").get(0).asText() : "";
                String weakness = cardNode.path("weaknesses").isArray() ? cardNode.path("weaknesses").get(0).path("type").asText() : "";

                // Télécharger l'image localement
                String imagePath = downloadImage(imageUrl, cardId);

                // Ajouter imagePath à l'appel
                Long cardIdInDb = pokemonCardService.savePokemonCard(cardId, "Pokemon", hp, type, energyType, weakness, imagePath);
                pokemonCardService.saveCardTranslation(cardIdInDb, "en", nameEn, flavorTextEn);
                logger.info("Carte Pokémon sauvegardée : {} dans le set {}", cardId, setCode);
            }
        } catch (Exception e) {
            logger.error("Erreur lors du traitement de la série Pokémon {} : {}", setCode, e.getMessage());
        }
    }

    private String downloadImage(String imageUrl, String cardId) {
        try {
            logger.debug("Tentative de téléchargement de l'image pour la carte {} depuis l'URL : {}", cardId, imageUrl);
            // Définir le chemin local pour stocker l'image
            String localPath = "target/classes/static/images/Pokemon/" + cardId + ".jpg";
            Path path = Paths.get(localPath);
            Files.createDirectories(path.getParent()); // Créer les dossiers si nécessaire
            logger.debug("Dossier créé pour l'image : {}", path.getParent());

            // Télécharger l'image
            URL url = new URL(imageUrl);
            try (InputStream in = url.openStream();
                 FileOutputStream out = new FileOutputStream(localPath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            logger.debug("Image téléchargée avec succès pour la carte {} : {}", cardId, localPath);

            // Retourner le chemin relatif pour le frontend
            return "/images/Pokemon/" + cardId + ".jpg";
        } catch (Exception e) {
            logger.error("Erreur lors du téléchargement de l'image pour la carte {} : {}", cardId, e.getMessage());
            return imageUrl; // Retourner l'URL distante en cas d'échec
        }
    }
}