package com.example.demo.entity;

import com.example.demo.entity.CardTranslation;
import com.example.demo.entity.PokemonCard;
import com.example.demo.entity.Set;
import com.example.demo.entity.YuGiOhCard;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "cards")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "rarity")
    private String rarity;

    @ManyToMany
    @JoinTable(
            name = "card_set",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "set_id")
    )
    private List<Set> sets = new ArrayList<>();

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardTranslation> translations = new ArrayList<>();

    // Getters et setters

    public void addTranslation(CardTranslation translation) { this.translations.add(translation); }

}