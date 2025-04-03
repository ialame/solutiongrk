package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
@Table(name = "card_set_translation")
public class CardSetTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_set_id")
    private CardSet cardSet;

    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(nullable = false)
    private String name;

    public CardSetTranslation() {}
    public CardSetTranslation(CardSet cardSet, Language language, String name) {
        this.cardSet = cardSet;
        this.language = language;
        this.name = name;
    }

    // Getters, setters

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CardSetTranslation)) return false;
        CardSetTranslation that = (CardSetTranslation) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(language, that.language) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, language, name); // Exclure cardSet
    }
}