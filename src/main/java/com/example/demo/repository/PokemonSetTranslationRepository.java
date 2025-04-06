package com.example.demo.repository;

import com.example.demo.entity.PokemonSet;
import com.example.demo.translation.PokemonSetTranslation;
import com.example.demo.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonSetTranslationRepository extends JpaRepository<PokemonSetTranslation, Long> {
    PokemonSetTranslation findByCardSetAndLanguage(PokemonSet cardSet, Language language);
}