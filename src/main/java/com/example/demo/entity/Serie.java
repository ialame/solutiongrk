package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "serie")
public class Serie {
    // Getters et setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_type", nullable = false)
    private String gameType;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SerieTranslation> translations = new HashSet<>();

    public Serie() {}

    public void addTranslation(SerieTranslation translation) {
        translations.add(translation);
        translation.setSerie(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gameType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Serie)) return false;
        Serie serie = (Serie) o;
        return Objects.equals(id, serie.id) && Objects.equals(gameType, serie.gameType);
    }
}