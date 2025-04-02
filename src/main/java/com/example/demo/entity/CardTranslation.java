package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "card_translations")
public class CardTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;
    // Getters et Setters

    public CardTranslation() {}
    public CardTranslation(Card card, Language language, String name, String description) {
        this.card = card;
        this.language = language;
        this.name = name;
        this.description = description;
    }
}
