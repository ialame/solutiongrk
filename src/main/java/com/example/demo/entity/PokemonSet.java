package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true) // Ajouté pour inclure Set
@Entity
@Table(name = "pokemon_sets")
@PrimaryKeyJoinColumn(name = "id")
public class PokemonSet extends Set {

    @Column(name = "release_date")
    private String releaseDate; // Format : "YYYY-MM-DD"

    @Column(name = "total_cards")
    private Integer totalCards;

    @Column(name = "legalities")
    private String legalities; // Ex. "Standard", "Expanded", "Unlimited"

    @Column(name = "ptcgo_code")
    private String ptcgoCode; // Code pour Pokémon TCG Online

    // Getters et setters

}
