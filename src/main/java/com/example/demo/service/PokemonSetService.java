package com.example.demo.service;

import com.example.demo.entity.PokemonSet;
import com.example.demo.translation.PokemonSetTranslation;
import com.example.demo.entity.Language;
import com.example.demo.repository.PokemonSetRepository;
import com.example.demo.repository.PokemonSetTranslationRepository;
import com.example.demo.repository.LanguageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PokemonSetService {

    private static final Logger logger = LoggerFactory.getLogger(PokemonSetService.class);

    @Autowired
    private PokemonSetRepository pokemonSetRepository;

    @Autowired
    private PokemonSetTranslationRepository pokemonSetTranslationRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Transactional
    public PokemonSet saveSet(String setCode, int totalCards) {
        Optional<PokemonSet> setOptional = pokemonSetRepository.findBySetCode(setCode);
        PokemonSet pokemonSet;
        if (setOptional.isEmpty()) {
            pokemonSet = new PokemonSet();
            pokemonSet.setSetCode(setCode);
            pokemonSet.setTotalCards(totalCards);
            pokemonSet = pokemonSetRepository.save(pokemonSet);
            logger.info("Set Pokémon sauvegardé : {} avec {} cartes (ID: {})", setCode, totalCards, pokemonSet.getId());
        } else {
            pokemonSet = setOptional.get();
            logger.debug("Set Pokémon {} déjà existant (ID: {})", setCode, pokemonSet.getId());
        }
        return pokemonSet;
    }

    @Transactional
    public void saveSetTranslation(Long setId, String languageCode, String name) {
        Optional<PokemonSet> setOptional = pokemonSetRepository.findById(setId);
        if (setOptional.isEmpty()) {
            throw new IllegalStateException("Set Pokémon ID " + setId + " non trouvé");
        }
        PokemonSet pokemonSet = setOptional.get();

        Language language = languageRepository.findByCode(languageCode);
        if (language == null) {
            language = new Language();
            language.setCode(languageCode);
            language = languageRepository.save(language);
        }

        PokemonSetTranslation existingTranslation = pokemonSetTranslationRepository.findByCardSetAndLanguage(pokemonSet, language);
        if (existingTranslation == null) {
            PokemonSetTranslation translation = new PokemonSetTranslation(pokemonSet, language, name);
            pokemonSet.getTranslations().add(translation);
            pokemonSetTranslationRepository.save(translation);
            logger.debug("Traduction sauvegardée pour set {} en {} : {}", setId, languageCode, name);
        } else {
            logger.debug("Traduction déjà existante pour set {} en {}", setId, languageCode);
        }
    }
}