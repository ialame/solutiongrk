package com.example.demo.repository;

import com.example.demo.entity.PokemonCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PokemonCardRepository extends JpaRepository<PokemonCard, Long> {
    List<PokemonCard> findByEnergyType(String energyType);
}
