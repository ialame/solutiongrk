package com.example.demo.repository;

import com.example.demo.entity.PokemonSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PokemonSetRepository extends JpaRepository<PokemonSet, Long> {
    Optional<PokemonSet> findBySetCode(String setCode);
    List<PokemonSet> findAll(); // Déjà inclus via JpaRepository
}