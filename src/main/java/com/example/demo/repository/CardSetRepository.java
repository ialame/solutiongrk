package com.example.demo.repository;

import com.example.demo.entity.CardSet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CardSetRepository extends JpaRepository<CardSet, Long> {
    Optional<CardSet> findBySetCode(String setCode);
}