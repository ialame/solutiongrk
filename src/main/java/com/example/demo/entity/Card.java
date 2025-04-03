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
public abstract class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private String gameType;

    @Column(nullable = false)
    private String rarity;

    @Column
    private String imagePath;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "card_set_card",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "card_set_id")
    )
    private Set<CardSet> sets = new HashSet<>();

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private Set<CardTranslation> translations = new HashSet<>();

    public void addTranslation(CardTranslation translation) { this.translations.add(translation); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id) &&
                Objects.equals(cardNumber, card.cardNumber) &&
                Objects.equals(gameType, card.gameType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cardNumber, gameType);
    }
}