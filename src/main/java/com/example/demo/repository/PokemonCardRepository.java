package com.example.demo.repository;

import com.example.demo.entity.PokemonCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PokemonCardRepository extends JpaRepository<PokemonCard, Long> {
    PokemonCard findByCardNumberAndGameType(String cardNumber, String gameType);
    List<PokemonCard> findAll(); // Déjà inclus via JpaRepository, juste pour clarté
}