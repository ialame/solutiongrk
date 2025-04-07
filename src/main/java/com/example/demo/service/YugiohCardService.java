package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.translation.YugiohCardTranslation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class YugiohCardService {

    private static final Logger logger = LoggerFactory.getLogger(YugiohCardService.class);

    @Autowired
    private YugiohCardRepository yugiohCardRepository;

    @Autowired
    private YugiohCardTranslationRepository yugiohCardTranslationRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Transactional
    public Long saveYugiohCard(String cardNumber, String gameType, Integer attack, Integer defense, String type) {
        YugiohCard card = new YugiohCard();
        card.setCardNumber(cardNumber);
        card.setGameType(gameType);
        card.setAttack(attack);
        card.setDefense(defense);
        card.setType(type);
        YugiohCard savedCard = yugiohCardRepository.save(card);
        return savedCard.getId();
    }

    @Transactional
    public void saveCardTranslation(Long cardId, String languageCode, String name, String description) {
        Optional<YugiohCard> cardOptional = yugiohCardRepository.findById(cardId);
        if (cardOptional.isEmpty()) {
            throw new IllegalStateException("Carte Yu-Gi-Oh ID " + cardId + " non trouvée");
        }
        YugiohCard card = cardOptional.get();

        Language language = languageRepository.findByCode(languageCode);
        if (language == null) {
            language = new Language();
            language.setCode(languageCode);
            language = languageRepository.save(language);
        }

        YugiohCardTranslation translation = new YugiohCardTranslation();
        translation.setCard(card);
        translation.setLanguage(language);
        translation.setName(name);
        translation.setDescription(description);
        card.getTranslations().add(translation);
        yugiohCardTranslationRepository.save(translation);
        logger.debug("Traduction sauvegardée pour carte Yu-Gi-Oh {} en {} : {}", cardId, languageCode, name);
    }

    @Transactional(readOnly = true)
    public List<YugiohCard> findAllCards() {
        logger.info("Récupération de toutes les cartes Yu-Gi-Oh");
        return yugiohCardRepository.findAll();
    }
}