package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "card")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "game_type", nullable = false)
    private String gameType;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "rarity")
    private String rarity;

    @ManyToMany
    @JoinTable(
            name = "card_set_card",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "card_set_id")
    )
    private Set<CardSet> sets = new HashSet<>();

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CardTranslation> translations = new HashSet<>();

    // Méthode pour ajouter une traduction
    public void addTranslation(CardTranslation translation) {
        translations.add(translation);
        translation.setCard(this);
    }

    // Getters, setters...
}