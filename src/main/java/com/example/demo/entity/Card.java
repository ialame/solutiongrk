package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "card")
@Inheritance(strategy = InheritanceType.JOINED)
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "rarity")
    private String rarity;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "game_type")
    private String gameType;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CardTranslation> translations = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "card_set_card",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "card_set_id")
    )
    private Set<CardSet> sets = new HashSet<>();

    // Constructeurs
    public Card() {}

    // Getters et setters

    public void addTranslation(CardTranslation translation) {
        translations.add(translation);
        translation.setCard(this);
    }

    // hashCode et equals sans récursion
    @Override
    public int hashCode() {
        return Objects.hash(id, cardNumber, rarity, imagePath, gameType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id) &&
                Objects.equals(cardNumber, card.cardNumber) &&
                Objects.equals(rarity, card.rarity) &&
                Objects.equals(imagePath, card.imagePath) &&
                Objects.equals(gameType, card.gameType);
    }
}