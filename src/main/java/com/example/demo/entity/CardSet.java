package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "card_set")
@Inheritance(strategy = InheritanceType.JOINED)
public class CardSet {
    // Getters et setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "set_code")
    private String setCode;

    @ManyToOne
    @JoinColumn(name = "serie_id")
    private Serie serie;

    @OneToMany(mappedBy = "cardSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CardSetTranslation> translations = new HashSet<>();

    @ManyToMany(mappedBy = "sets")
    private Set<Card> cards = new HashSet<>();

    public CardSet() {}

    // Méthode pour ajouter une traduction
    public void addTranslation(CardSetTranslation translation) {
        translations.add(translation);
        translation.setCardSet(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, setCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CardSet)) return false;
        CardSet cardSet = (CardSet) o;
        return Objects.equals(id, cardSet.id) &&
                Objects.equals(setCode, cardSet.setCode);
    }
}