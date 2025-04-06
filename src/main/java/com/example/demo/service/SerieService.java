package com.example.demo.service;

import com.example.demo.entity.Serie;
import com.example.demo.translation.SerieTranslation;
import com.example.demo.entity.Language;
import com.example.demo.repository.SerieRepository;
import com.example.demo.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private LanguageRepository languageRepository;

    public Serie saveSerie(String gameType, String name) { // Changement de void à Serie
        Serie serie = new Serie();
        serie.setGameType(gameType);

        Language english = languageRepository.findByCode("en");
        if (english == null) {
            english = new Language();
            english.setCode("en");
            english = languageRepository.save(english);
        }

        SerieTranslation translation = new SerieTranslation();
        translation.setSerie(serie);
        translation.setLanguage(english);
        translation.setName(name);

        serie.getTranslations().add(translation);
        return serieRepository.save(serie); // Retourner la série sauvegardée
    }

    public Optional<Serie> findByNameAndGameType(String name, String gameType) {
        return serieRepository.findByNameAndGameType(name, gameType);
    }
}