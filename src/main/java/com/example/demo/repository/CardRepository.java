package com.example.demo.repository;

import com.example.demo.Language;
import com.example.demo.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumberAndSets_SetCode(String cardNumber, String setCode);
    List<Card> findByTranslations_Language(Language language);
}
