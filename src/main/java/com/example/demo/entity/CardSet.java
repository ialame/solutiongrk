package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@Setter
@Getter
@Entity
@Table(name = "card_set")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CardSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "set_code")
    private String setCode;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "total_cards")
    private Integer totalCards;

    @ManyToMany(mappedBy = "cardSets", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Card> cards = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "serie_id")
    private Serie serie;

    @OneToMany(mappedBy = "cardSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CardSetTranslation> translations = new HashSet<>();

    public CardSet() {}

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