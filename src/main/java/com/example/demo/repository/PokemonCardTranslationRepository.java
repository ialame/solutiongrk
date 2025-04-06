package com.example.demo.repository;

import com.example.demo.entity.Card;
import com.example.demo.translation.PokemonCardTranslation;
import com.example.demo.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonCardTranslationRepository extends JpaRepository<PokemonCardTranslation, Long> {
    PokemonCardTranslation findByCardAndLanguage(Card card, Language language);
}