package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.SerieRepository;
import com.example.demo.repository.CardSetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntityPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(EntityPersistenceService.class);

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private CardSetRepository cardSetRepository;

    @Autowired
    private CardRepository cardRepository;

    @Transactional
    public Serie saveSerie(String serieName, String gameType) {
        logger.debug("Tentative de sauvegarde de la série : {} (gameType: {})", serieName, gameType);
        Serie serie = serieRepository.findByTranslations_NameAndGameType(serieName, gameType)
                .orElseGet(() -> {
                    Serie newSerie = new Serie();
                    newSerie.setGameType(gameType);
                    newSerie.addTranslation(new SerieTranslation(newSerie, Language.US, serieName));
                    Serie savedSerie = serieRepository.save(newSerie);
                    serieRepository.flush();
                    logger.info("Série sauvegardée : {} (ID: {})", serieName, savedSerie.getId());
                    return savedSerie;
                });
        return serie;
    }

    @Transactional
    public CardSet saveSet(String setCode, String setName, Serie serie, String ptcgoCode, String releaseDate, Integer totalCards, String legalities) {
        logger.debug("Tentative de sauvegarde du set : {} (setName: {})", setCode, setName);
        CardSet existingSet = cardSetRepository.findBySetCode(setCode).orElse(null);
        if (existingSet != null) {
            logger.debug("Set existant trouvé : {} (ID: {})", setCode, existingSet.getId());
            return existingSet;
        }

        CardSet newSet;
        switch (serie.getGameType()) {
            case "Pokemon":
                newSet = new PokemonSet();
                ((PokemonSet) newSet).setPtcgoCode(ptcgoCode);
                ((PokemonSet) newSet).setReleaseDate(releaseDate);
                ((PokemonSet) newSet).setTotalCards(totalCards);
                ((PokemonSet) newSet).setLegalities(legalities);
                break;
            default:
                throw new IllegalArgumentException("Type de jeu non supporté : " + serie.getGameType());
        }
        newSet.setSetCode(setCode);
        newSet.setSerie(serie);
        newSet.addTranslation(new CardSetTranslation(newSet, Language.US, setName));

        try {
            CardSet savedSet = cardSetRepository.save(newSet);
            cardSetRepository.flush();
            logger.info("Set sauvegardé : {} (ID: {})", setCode, savedSet.getId());
            return savedSet;
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde du set {} : {}", setCode, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Card saveCard(Card card, CardSet set) {
        logger.debug("Tentative de sauvegarde de la carte : {} avec set : {}", card.getCardNumber(), set.getSetCode());
        Card existingCard = cardRepository.findByCardNumberAndSets_SetCode(card.getCardNumber(), set.getSetCode())
                .orElseGet(() -> {
                    try {
                        CardSet managedSet = cardSetRepository.findById(set.getId())
                                .orElseThrow(() -> new IllegalStateException("Set non trouvé : " + set.getSetCode()));
                        card.getSets().add(managedSet);
                        managedSet.getCards().add(card);
                        Card savedCard = cardRepository.save(card);
                        cardRepository.flush();
                        logger.info("Carte sauvegardée : {} (ID: {}) avec set {}", card.getCardNumber(), savedCard.getId(), managedSet.getSetCode());
                        return savedCard;
                    } catch (Exception e) {
                        logger.error("Erreur lors de la sauvegarde de la carte {} : {}", card.getCardNumber(), e.getMessage(), e);
                        throw e;
                    }
                });
        return existingCard;
    }
}