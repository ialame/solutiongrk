package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "card_set")
public abstract class CardSet {

    // Getters, setters, addTranslation
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String setCode;

    @ManyToOne
    @JoinColumn(name = "serie_id")
    private Serie serie;

    @ManyToMany(mappedBy = "sets")
    private Set<Card> cards = new HashSet<>();

    @OneToMany(mappedBy = "cardSet", cascade = CascadeType.ALL)
    private Set<CardSetTranslation> translations = new HashSet<>();

    public void addTranslation(CardSetTranslation translation) { this.translations.add(translation); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CardSet)) return false;
        CardSet cardSet = (CardSet) o;
        return Objects.equals(id, cardSet.id) && Objects.equals(setCode, cardSet.setCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, setCode); // Exclure translations et cards
    }
}