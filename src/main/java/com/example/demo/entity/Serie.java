// Serie.java
package com.example.demo.entity;

import com.example.demo.Language;
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
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "game_type", nullable = false)
    private String gameType;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SerieTranslation> translations = new ArrayList<>();

    @OneToMany(mappedBy = "serie")
    private List<Set> sets = new ArrayList<>();

    public Serie() {
    }

    public void addTranslation(Language language, String name) {
        SerieTranslation translation = new SerieTranslation(this, language, name);
        this.translations.add(translation);
    }

    // Getters et Setters

}