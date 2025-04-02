package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "series")
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_type", nullable = false)
    private String gameType; // Ex. "Pokemon", "Yu-Gi-Oh!"

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SerieTranslation> translations = new ArrayList<>();

    // Getters et setters
    public void addTranslation(SerieTranslation translation) { this.translations.add(translation); }
}