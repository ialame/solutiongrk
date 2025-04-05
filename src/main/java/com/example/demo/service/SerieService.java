package com.example.demo.service;

import com.example.demo.entity.Language;
import com.example.demo.entity.Serie;
import com.example.demo.entity.SerieTranslation;
import com.example.demo.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SerieService {
    @Autowired
    private SerieRepository serieRepository;

    public Serie saveSerie(String name, String gameType) {
        return serieRepository.findByNameAndGameType(name, gameType)
                .orElseGet(() -> {
                    Serie serie = new Serie();
                    serie.setGameType(gameType);
                    serie.addTranslation(new SerieTranslation(serie, Language.EN, name));
                    return serieRepository.save(serie);
                });
    }

    public Optional<Serie> findByNameAndGameType(String name, String gameType) {
        return serieRepository.findByNameAndGameType(name, gameType);
    }
}