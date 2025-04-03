package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.CardSetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CardSetService {

    private static final Logger logger = LoggerFactory.getLogger(CardSetService.class);

    @Autowired
    private CardSetRepository cardSetRepository;

    @Transactional
    public CardSet createCardSet(String setCode, String setName, String gameType) {
        CardSet cardSet;
        switch (gameType) {
            case "Pokemon":
                cardSet = new PokemonSet();
                break;
            // Ajouter d'autres cas : YuGiOh, Lorcana, etc.
            default:
                throw new IllegalArgumentException("Type de jeu non supporté : " + gameType);
        }
        cardSet.setSetCode(setCode);
        cardSet.addTranslation(new CardSetTranslation(cardSet, Language.US, setName));
        // Note : Si une Serie est requise, vous devrez la passer en paramètre ou la récupérer
        CardSet savedSet = cardSetRepository.save(cardSet);
        logger.info("CardSet créé : {} (ID: {})", setCode, savedSet.getId());
        return savedSet;
    }

    public Optional<CardSet> findBySetCode(String setCode) {
        return cardSetRepository.findBySetCode(setCode);
    }

    public List<CardSet> findAll() {
        return cardSetRepository.findAll();
    }

    @Transactional
    public void deleteCardSet(Long id) {
        cardSetRepository.deleteById(id);
        logger.info("CardSet supprimé : ID {}", id);
    }
}