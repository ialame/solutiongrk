package com.example.demo.service;

import com.example.demo.entity.PokemonSet;
import com.example.demo.entity.PokemonCard;
import com.example.demo.entity.CardTranslation;
import com.example.demo.entity.PokemonCardTranslation;
import com.example.demo.entity.Language;
import com.example.demo.repository.PokemonSetRepository;
import com.example.demo.repository.PokemonCardRepository;
import com.example.demo.repository.PokemonCardTranslationRepository; // Changement ici
import com.example.demo.repository.LanguageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PokemonCardService {

    private static final Logger logger = LoggerFactory.getLogger(PokemonCardService.class);

    @Autowired
    private PokemonSetRepository pokemonSetRepository;

    @Autowired
    private PokemonCardRepository pokemonCardRepository;

    @Autowired
    private PokemonCardTranslationRepository cardTranslationRepository; // Changement ici

    @Autowired
    private LanguageRepository languageRepository;

    public PokemonSetRepository getPokemonSetRepository() {
        return pokemonSetRepository;
    }

    @Transactional
    public PokemonSet saveSet(String setCode, int totalCards) {
        Optional<PokemonSet> setOptional = pokemonSetRepository.findBySetCode(setCode);
        PokemonSet pokemonSet;
        if (setOptional.isEmpty()) {
            pokemonSet = new PokemonSet();
            pokemonSet.setSetCode(setCode);
            pokemonSet.setTotalCards(totalCards);
            pokemonSet = pokemonSetRepository.save(pokemonSet);
            logger.info("Set sauvegardé : {} avec {} cartes (ID: {})", setCode, totalCards, pokemonSet.getId());
        } else {
            pokemonSet = setOptional.get();
            logger.debug("Set {} déjà existant (ID: {})", setCode, pokemonSet.getId());
        }
        return pokemonSet;
    }

    @Transactional
    public Long savePokemonCard(String cardId, String setCode, String hp, String energyType, String weakness) {
        Optional<PokemonSet> setOptional = pokemonSetRepository.findBySetCode(setCode);
        if (setOptional.isEmpty()) {
            throw new IllegalStateException("Set " + setCode + " non trouvé");
        }
        PokemonSet pokemonSet = setOptional.get();

        PokemonCard existingCard = pokemonCardRepository.findByCardNumberAndGameType(cardId, "Pokemon");
        if (existingCard == null) {
            PokemonCard card = new PokemonCard();
            card.setCardNumber(cardId);
            card.setGameType("Pokemon");
            card.setImagePath("/images/Pokemon/" + cardId + ".jpg");
            card.setRarity("Unknown");
            card.setHp(hp != null && !hp.equals("Unknown") ? Integer.parseInt(hp) : null);
            card.setEnergyType(energyType);
            card.setWeakness(weakness);

            card.getCardSets().add(pokemonSet);
            pokemonSet.getCards().add(card);

            card = pokemonCardRepository.save(card);
            pokemonSetRepository.save(pokemonSet);

            logger.info("Carte Pokémon sauvegardée : {} dans le set {} (ID: {})", cardId, setCode, card.getId());
            return card.getId();
        } else {
            if (!existingCard.getCardSets().contains(pokemonSet)) {
                existingCard.getCardSets().add(pokemonSet);
                pokemonSet.getCards().add(existingCard);
                pokemonCardRepository.save(existingCard);
                pokemonSetRepository.save(pokemonSet);
                logger.debug("Carte {} liée au set {}", cardId, setCode);
            }
            logger.debug("Carte {} déjà existante (ID: {})", cardId, existingCard.getId());
            return existingCard.getId();
        }
    }

    @Transactional
    public void saveCardTranslation(Long cardId, String languageCode, String name, String flavorText) {
        Optional<PokemonCard> cardOptional = pokemonCardRepository.findById(cardId);
        if (cardOptional.isEmpty()) {
            throw new IllegalStateException("Carte ID " + cardId + " non trouvée");
        }
        PokemonCard card = cardOptional.get();

        Language language = languageRepository.findByCode(languageCode);
        if (language == null) {
            language = new Language();
            language.setCode(languageCode);
            language = languageRepository.save(language);
        }

        PokemonCardTranslation existingTranslation = cardTranslationRepository.findByCardAndLanguage(card, language);
        if (existingTranslation == null) {
            // Utilisation du constructeur
            PokemonCardTranslation translation = new PokemonCardTranslation(card, language, name, "Card: " + name, flavorText);
            card.getTranslations().add(translation);
            cardTranslationRepository.save(translation);
            logger.debug("Traduction sauvegardée pour carte {} en {} : {} (Flavor: {})",
                    cardId, languageCode, name, flavorText);
        } else {
            logger.debug("Traduction déjà existante pour carte {} en {}", cardId, languageCode);
        }
    }
}