package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "card")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public abstract class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "game_type")
    private String gameType;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "rarity")
    private String rarity;

    @ManyToMany
    @JoinTable(
            name = "card_card_set",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "card_set_id")
    )
    private Set<CardSet> cardSets = new HashSet<>();
}