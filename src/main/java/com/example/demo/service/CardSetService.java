package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.CardSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CardSetService {
    @Autowired
    private CardSetRepository cardSetRepository;

    @Autowired
    private SerieService serieService;

    public CardSet createCardSet(String setCode, String name, String serieName, String gameType, String ptcgoCode, String releaseDate, Integer totalCards, String legalities) {
        Serie serie = serieService.saveSerie(serieName, gameType); // Changement ici
        PokemonSet cardSet = new PokemonSet();
        cardSet.setSetCode(setCode);
        cardSet.setSerie(serie);
        cardSet.addTranslation(new CardSetTranslation(cardSet, Language.EN, name));
        cardSet.setPtcgoCode(ptcgoCode);
        cardSet.setReleaseDate(releaseDate);
        cardSet.setTotalCards(totalCards);
        cardSet.setLegalities(legalities);
        return cardSetRepository.save(cardSet);
    }

    public Optional<CardSet> findBySetCode(String setCode) {
        return cardSetRepository.findBySetCode(setCode);
    }

    public List<CardSet> findAll() {
        return cardSetRepository.findAll();
    }

    public void deleteCardSet(Long id) {
        cardSetRepository.deleteById(id);
    }
}