package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "pokemon_cards")
@PrimaryKeyJoinColumn(name = "id")
public class PokemonCard extends Card {

    @Column(name = "energy_type")
    private String energyType;

    @Column(name = "hp")
    private Integer hp;

    @Column(name = "weakness")
    private String weakness;

    // Getters et Setters

}
