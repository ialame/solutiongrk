package com.example.demo.entity;

import com.example.demo.entity.CardTranslation;
import com.example.demo.entity.PokemonCard;
import com.example.demo.entity.Set;
import com.example.demo.entity.YuGiOhCard;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "cards")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PokemonCard.class, name = "PokemonCard"),
        @JsonSubTypes.Type(value = YuGiOhCard.class, name = "YuGiOhCard")
})
public abstract class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private String rarity;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<CardTranslation> translations;

    @ManyToMany
    @JoinTable(
            name = "card_set",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "set_id")
    )
    private List<Set> sets;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<CardSet> cardSets;

    @Column
    private String imagePath;
    // Constructeur par défaut requis par JPA
    public Card() {}

    // Getters et Setters

}