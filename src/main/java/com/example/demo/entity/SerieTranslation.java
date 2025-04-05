package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "serie_translation")
public class SerieTranslation {
    // Getters et setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Language language;

    @Column(name = "name", nullable = false)
    private String name;

    public SerieTranslation() {}

    public SerieTranslation(Serie serie, Language language, String name) {
        this.serie = serie;
        this.language = language;
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, language, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SerieTranslation)) return false;
        SerieTranslation that = (SerieTranslation) o;
        return Objects.equals(id, that.id) &&
                language == that.language &&
                Objects.equals(name, that.name);
    }
}