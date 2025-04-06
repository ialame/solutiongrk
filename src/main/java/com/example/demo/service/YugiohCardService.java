package com.example.demo.service;

import com.example.demo.entity.YugiohCard;
import com.example.demo.entity.YugiohSet;
import com.example.demo.translation.YugiohCardTranslation;
import com.example.demo.entity.Language;
import com.example.demo.repository.YugiohSetRepository;
import com.example.demo.repository.YugiohCardRepository;
import com.example.demo.repository.YugiohCardTranslationRepository;
import com.example.demo.repository.LanguageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class YugiohCardService {

    private static final Logger logger = LoggerFactory.getLogger(YugiohCardService.class);

    @Autowired
    private YugiohSetRepository yugiohSetRepository;

    @Autowired
    private YugiohCardRepository yugiohCardRepository;

    @Autowired
    private YugiohCardTranslationRepository cardTranslationRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Transactional
    public Long saveYugiohCard(String cardId, String setCode, Integer attack, Integer defense, String type) {
        Optional<YugiohSet> setOptional = yugiohSetRepository.findBySetCode(setCode);
        if (setOptional.isEmpty()) {
            throw new IllegalStateException("Set Yu-Gi-Oh " + setCode + " non trouvé");
        }
        YugiohSet yugiohSet = setOptional.get();

        YugiohCard existingCard = yugiohCardRepository.findByCardNumberAndGameType(cardId, "Yugioh");
        if (existingCard == null) {
            YugiohCard card = new YugiohCard();
            card.setCardNumber(cardId);
            card.setGameType("Yugioh");
            card.setImagePath("/images/Yugioh/" + cardId + ".jpg");
            card.setAttack(attack);
            card.setDefense(defense);
            card.setType(type);

            card.getCardSets().add(yugiohSet);
            yugiohSet.getCards().add(card);

            card = yugiohCardRepository.save(card);
            yugiohSetRepository.save(yugiohSet);

            logger.info("Carte Yu-Gi-Oh sauvegardée : {} dans le set {} (ID: {})", cardId, setCode, card.getId());
            return card.getId();
        } else {
            if (!existingCard.getCardSets().contains(yugiohSet)) {
                existingCard.getCardSets().add(yugiohSet);
                yugiohSet.getCards().add(existingCard);
                yugiohCardRepository.save(existingCard);
                yugiohSetRepository.save(yugiohSet);
                logger.debug("Carte {} liée au set {}", cardId, setCode);
            }
            logger.debug("Carte {} déjà existante (ID: {})", cardId, existingCard.getId());
            return existingCard.getId();
        }
    }

    @Transactional
    public void saveCardTranslation(Long cardId, String languageCode, String name) {
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

        YugiohCardTranslation existingTranslation = cardTranslationRepository.findByCardAndLanguage_Id(card, language.getId());
        if (existingTranslation == null) {
            String description = "Card: " + name;
            logger.info("Sauvegarde de la traduction pour carte {} en {} : name='{}' (length={}), description='{}' (length={})",
                    cardId, languageCode, name, name.length(), description, description.length());
            YugiohCardTranslation translation = new YugiohCardTranslation(card, language, name, description);
            logger.info("Après instanciation : translation.name = '{}'", translation.getName());
            card.getTranslations().add(translation);
            YugiohCardTranslation savedTranslation = cardTranslationRepository.saveAndFlush(translation);
            logger.debug("Traduction sauvegardée pour carte {} en {} : {}", cardId, languageCode, name);
            logger.info("Vérification post-sauvegarde : ID de la traduction = {}", savedTranslation.getId());
        } else {
            logger.debug("Traduction déjà existante pour carte {} en {} : {}", cardId, languageCode, existingTranslation.getName());
        }
    }
}