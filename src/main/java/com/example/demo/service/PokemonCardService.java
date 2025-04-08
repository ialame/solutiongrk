package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.translation.PokemonCardTranslation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PokemonCardService {

    private static final Logger logger = LoggerFactory.getLogger(PokemonCardService.class);

    @Autowired
    private PokemonCardRepository pokemonCardRepository;

    @Autowired
    private PokemonCardTranslationRepository pokemonCardTranslationRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Transactional
    public Long savePokemonCard(String cardNumber, String gameType, Integer hp, String type, String energyType, String weakness, String imagePath) {
        PokemonCard card = new PokemonCard();
        card.setCardNumber(cardNumber);
        card.setGameType(gameType);
        card.setHp(hp);
        card.setType(type);
        card.setEnergyType(energyType);
        card.setWeakness(weakness);
        card.setImagePath(imagePath); // Ajout de l'imagePath
        PokemonCard savedCard = pokemonCardRepository.save(card);
        return savedCard.getId();
    }

    @Transactional
    public void saveCardTranslation(Long cardId, String languageCode, String name, String flavorText) {
        Optional<PokemonCard> cardOptional = pokemonCardRepository.findById(cardId);
        if (cardOptional.isEmpty()) {
            throw new IllegalStateException("Carte Pokémon ID " + cardId + " non trouvée");
        }
        PokemonCard card = cardOptional.get();

        Language language = languageRepository.findByCode(languageCode);
        if (language == null) {
            language = new Language();
            language.setCode(languageCode);
            language = languageRepository.save(language);
        }

        PokemonCardTranslation translation = new PokemonCardTranslation();
        translation.setCard(card);
        translation.setLanguage(language);
        translation.setName(name);
        translation.setDescription(flavorText);
        card.getTranslations().add(translation);
        pokemonCardTranslationRepository.save(translation);
        logger.debug("Traduction sauvegardée pour carte Pokémon {} en {} : {}", cardId, languageCode, name);
    }

    @Transactional(readOnly = true)
    public List<PokemonCard> findAllCards() {
        logger.info("Récupération de toutes les cartes Pokémon");
        return pokemonCardRepository.findAll();
    }
}