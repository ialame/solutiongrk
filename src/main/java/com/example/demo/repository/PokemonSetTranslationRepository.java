package com.example.demo.repository;

import com.example.demo.translation.PokemonSetTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PokemonSetTranslationRepository extends JpaRepository<PokemonSetTranslation, Long> {
    List<PokemonSetTranslation> findByCardSetId(Long cardSetId);
}