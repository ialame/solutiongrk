package com.example.demo.repository;

import com.example.demo.translation.PokemonCardTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonCardTranslationRepository extends JpaRepository<PokemonCardTranslation, Long> {
}