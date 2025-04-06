package com.example.demo.translation;

import com.example.demo.entity.Card;
import com.example.demo.entity.Language;
import jakarta.persistence.*;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class CardTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id")
    protected Card card;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    protected Language language;

    @Column(name = "name")
    protected String name;

    @Column(name = "description")
    protected String description;

    @Column(name = "flavor_text")
    protected String flavorText;

    // Constructeur par défaut requis par JPA/Hibernate
    public CardTranslation() {
    }

    // Constructeur pour initialiser les champs principaux
    public CardTranslation(Card card, Language language, String name, String description) {
        this.card = card;
        this.language = language;
        this.name = name;
        this.description = description;
    }
}