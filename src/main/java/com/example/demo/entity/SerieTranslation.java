package com.example.demo.entity;

import com.example.demo.Language;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "serie_translations")
public class SerieTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;

    @Column(nullable = false)
    private String name;

    // Constructeurs
    public SerieTranslation() {
    }

    public SerieTranslation(Serie serie, Language language, String name) {
        this.serie = serie;
        this.language = language;
        this.name = name;
    }

    // Getters et Setters

}