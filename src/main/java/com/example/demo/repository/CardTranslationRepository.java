package com.example.demo.repository;

import com.example.demo.entity.Card;
import com.example.demo.entity.CardTranslation;
import com.example.demo.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardTranslationRepository extends JpaRepository<CardTranslation, Long> {
    CardTranslation findByCardAndLanguage(Card card, Language language);
}