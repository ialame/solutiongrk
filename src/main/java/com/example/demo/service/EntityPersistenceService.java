package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntityPersistenceService {
    private static final Logger logger = LoggerFactory.getLogger(EntityPersistenceService.class);

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private CardSetRepository cardSetRepository;

    @Autowired
    private CardRepository cardRepository;

    public Serie saveSerie(String name, String gameType) {
        Serie serie = serieRepository.findByNameAndGameType(name, gameType)
                .orElseGet(() -> {
                    Serie newSerie = new Serie();
                    newSerie.setGameType(gameType);
                    newSerie.addTranslation(new SerieTranslation(newSerie, Language.EN, name));
                    return serieRepository.save(newSerie);
                });
        logger.info("Série sauvegardée : {} (ID: {})", name, serie.getId());
        return serie;
    }

    public CardSet saveSet(String setCode, String name, Serie serie, String ptcgoCode, String releaseDate, Integer totalCards, String legalities) {
        CardSet existingSet = cardSetRepository.findBySetCode(setCode)
                .orElseGet(() -> {
                    PokemonSet newSet = new PokemonSet();
                    newSet.setSetCode(setCode);
                    newSet.setSerie(serie);
                    newSet.setPtcgoCode(ptcgoCode);
                    newSet.setReleaseDate(releaseDate);
                    newSet.setTotalCards(totalCards);
                    newSet.setLegalities(legalities);
                    newSet.addTranslation(new CardSetTranslation(newSet, Language.EN, name));
                    return cardSetRepository.save(newSet);
                });
        logger.info("Set sauvegardé : {} (ID: {})", setCode, existingSet.getId());
        return existingSet;
    }

    public void saveCard(Card card, CardSet set) {
        Card existingCard = cardRepository.findByCardNumberAndSet(card.getCardNumber(), set.getSetCode())
                .orElseGet(() -> {
                    card.getSets().add(set);
                    return cardRepository.save(card);
                });
        logger.info("Carte sauvegardée : {} dans le set {} (ID: {})", card.getCardNumber(), set.getSetCode(), existingCard.getId());
    }
}