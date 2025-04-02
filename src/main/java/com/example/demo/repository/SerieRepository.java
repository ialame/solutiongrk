package com.example.demo.repository;

import com.example.demo.entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    @Query("SELECT s FROM Serie s JOIN s.translations t WHERE t.name = :name AND s.gameType = :gameType")
    Optional<Serie> findByTranslations_NameAndGameType(@Param("name") String name, @Param("gameType") String gameType);

    List<Serie> findByGameType(String gameType); // Recherche par gameType dans Serie
}