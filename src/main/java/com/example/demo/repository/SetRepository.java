package com.example.demo.repository;

import com.example.demo.entity.Set;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SetRepository extends JpaRepository<Set, Long> {
    Optional<Set> findBySetCode(String setCode);

    // Nouvelle méthode
    List<Set> findByGameType(String gameType);
}