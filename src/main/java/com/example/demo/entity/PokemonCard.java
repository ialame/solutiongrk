package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "pokemon_cards")
public class PokemonCard extends Card {

    @Column(nullable = false)
    private String energyType; // Type d'énergie (ex. "Électrique", "Feu")

    @Column(nullable = false)
    private int hp; // Points de vie

    @Column
    private String weakness; // Faiblesse (ex. "Eau")

    // Constructeur par défaut
    public PokemonCard() {}
    // Getters et Setters

}
