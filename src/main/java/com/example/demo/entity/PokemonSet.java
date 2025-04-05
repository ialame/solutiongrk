package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
@Table(name = "pokemon_set")
public class PokemonSet extends CardSet {
    @Column(name = "legalities")
    private String legalities;

    @Column(name = "ptcgo_code")
    private String ptcgoCode;

    @Column(name = "total_cards")
    private Integer totalCards;

    public PokemonSet() {}

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), legalities, ptcgoCode, totalCards);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PokemonSet)) return false;
        if (!super.equals(o)) return false;
        PokemonSet that = (PokemonSet) o;
        return Objects.equals(legalities, that.legalities) &&
                Objects.equals(ptcgoCode, that.ptcgoCode) &&
                Objects.equals(totalCards, that.totalCards);
    }
}