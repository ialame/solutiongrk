// SerieRepository.java
package com.example.demo.repository;

import com.example.demo.Language;
import com.example.demo.entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTranslations_LanguageAndTranslations_Name(Language language, String name);
    List<Serie> findByGameType(String gameType);
}