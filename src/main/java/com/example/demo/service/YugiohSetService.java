package com.example.demo.service;

import com.example.demo.entity.YugiohSet;
import com.example.demo.translation.YugiohSetTranslation;
import com.example.demo.entity.Language;
import com.example.demo.repository.YugiohSetRepository;
import com.example.demo.repository.YugiohSetTranslationRepository;
import com.example.demo.repository.LanguageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class YugiohSetService {

    private static final Logger logger = LoggerFactory.getLogger(YugiohSetService.class);

    @Autowired
    private YugiohSetRepository yugiohSetRepository;

    @Autowired
    private YugiohSetTranslationRepository yugiohSetTranslationRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Transactional
    public YugiohSet saveSet(String setCode, int totalCards) {
        Optional<YugiohSet> setOptional = yugiohSetRepository.findBySetCode(setCode);
        YugiohSet yugiohSet;
        if (setOptional.isEmpty()) {
            yugiohSet = new YugiohSet();
            yugiohSet.setSetCode(setCode);
            yugiohSet.setTotalCards(totalCards);
            yugiohSet = yugiohSetRepository.save(yugiohSet);
            logger.info("Set Yu-Gi-Oh sauvegardé : {} avec {} cartes (ID: {})", setCode, totalCards, yugiohSet.getId());
        } else {
            yugiohSet = setOptional.get();
            logger.debug("Set Yu-Gi-Oh {} déjà existant (ID: {})", setCode, yugiohSet.getId());
        }
        return yugiohSet;
    }

    @Transactional
    public void saveSetTranslation(Long setId, String languageCode, String name) {
        Optional<YugiohSet> setOptional = yugiohSetRepository.findById(setId);
        if (setOptional.isEmpty()) {
            throw new IllegalStateException("Set Yu-Gi-Oh ID " + setId + " non trouvé");
        }
        YugiohSet yugiohSet = setOptional.get();

        Language language = languageRepository.findByCode(languageCode);
        if (language == null) {
            language = new Language();
            language.setCode(languageCode);
            language = languageRepository.save(language);
        }

        YugiohSetTranslation existingTranslation = yugiohSetTranslationRepository.findByCardSetAndLanguage(yugiohSet, language);
        if (existingTranslation == null) {
            YugiohSetTranslation translation = new YugiohSetTranslation(yugiohSet, language, name);
            yugiohSet.getTranslations().add(translation);
            yugiohSetTranslationRepository.save(translation);
            logger.debug("Traduction sauvegardée pour set {} en {} : {}", setId, languageCode, name);
        } else {
            logger.debug("Traduction déjà existante pour set {} en {}", setId, languageCode);
        }
    }
}