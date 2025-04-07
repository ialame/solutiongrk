package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.translation.YugiohSetTranslation;
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
    public Long saveSet(String setCode, int totalCards) {
        YugiohSet yugiohSet = new YugiohSet();
        yugiohSet.setSetCode(setCode);
        yugiohSet.setTotalCards(totalCards);
        YugiohSet savedSet = yugiohSetRepository.save(yugiohSet);
        return savedSet.getId(); // Retourne l'ID (Long)
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

        YugiohSetTranslation translation = new YugiohSetTranslation();
        translation.setCardSet(yugiohSet);
        translation.setLanguage(language);
        translation.setName(name);
        yugiohSetTranslationRepository.save(translation);
        logger.debug("Traduction sauvegardée pour le set Yu-Gi-Oh {} en {} : {}", setId, languageCode, name);
    }
}