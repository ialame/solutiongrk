package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.SerieRepository;
import com.example.demo.repository.SetRepository;
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
    private SetRepository setRepository;

    @Autowired
    private CardRepository cardRepository;

    @Transactional
    public Serie saveSerie(String serieName, String gameType) {
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
    public Set saveSet(String setCode, String setName, Serie serie) {
        Set set = setRepository.findBySetCode(setCode)
                .orElseGet(() -> {
                    PokemonSet newSet = new PokemonSet();
                    newSet.setSetCode(setCode);
                    newSet.setSerie(serie);
                    newSet.addTranslation(new SetTranslation(newSet, Language.US, setName));
                    Set savedSet = setRepository.save(newSet);
                    setRepository.flush();
                    logger.info("Set sauvegardé : {} (ID: {})", setCode, savedSet.getId());
                    return savedSet;
                });
        return set;
    }

    @Transactional
    public Card saveCard(PokemonCard card, Set set) {
        Card existingCard = cardRepository.findByCardNumberAndSets_SetCode(card.getCardNumber(), set.getSetCode())
                .orElseGet(() -> {
                    card.getSets().add(set);
                    set.getCards().add(card);
                    Card savedCard = cardRepository.save(card);
                    cardRepository.flush();
                    logger.info("Carte sauvegardée : {} (ID: {})", card.getCardNumber(), savedCard.getId());
                    return savedCard;
                });
        return existingCard;
    }
}
