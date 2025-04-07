package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@EqualsAndHashCode(callSuper = false, exclude = {"cardSets"})
public abstract class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_seq")
    @SequenceGenerator(name = "card_seq", sequenceName = "card_sequence", allocationSize = 1)
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
    @JsonIgnore
    private Set<CardSet> cardSets = new HashSet<>();
}