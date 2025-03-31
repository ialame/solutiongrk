package com.example.demo.repository;

import com.example.demo.entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    List<Serie> findByGameType(String gameType);
    Optional<Serie> findByNameAndGameType(String name, String gameType);
}
