package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Entity
@Table(name = "pokemon_set")
public class PokemonSet extends CardSet {

    @Column(name = "release_date")
    private String releaseDate; // Format : "YYYY-MM-DD"

    @Column(name = "total_cards")
    private Integer totalCards;

    @Column(name = "legalities")
    private String legalities; // Ex. "Standard", "Expanded", "Unlimited"

    @Column(name = "ptcgo_code")
    private String ptcgoCode; // Code pour Pokémon TCG Online


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PokemonSet)) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode(); // Réutilise le hashCode de CardSet
    }
    // Getters et setters

}
