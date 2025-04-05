package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
@Table(name = "card_translation")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CardTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "language", nullable = false)
    private Language language;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    // Constructeurs
    public CardTranslation() {}

    public CardTranslation(Card card, Language language, String name, String description) {
        this.card = card;
        this.language = language;
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CardTranslation)) return false;
        CardTranslation that = (CardTranslation) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(language, that.language) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, language, name); // Exclure card
    }
}