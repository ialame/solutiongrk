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

    public CardSet() {}
}