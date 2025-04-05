package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
@Table(name = "card_translation")
@Inheritance(strategy = InheritanceType.JOINED)
public class CardTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private Language language;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    public CardTranslation() {}

    public CardTranslation(Card card, Language language, String name, String description) {
        this.card = card;
        this.language = language;
        this.name = name;
        this.description = description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, language, name, description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CardTranslation)) return false;
        CardTranslation that = (CardTranslation) o;
        return Objects.equals(id, that.id) &&
                language == that.language &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
    }
}