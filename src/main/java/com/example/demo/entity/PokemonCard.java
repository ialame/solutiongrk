package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
@Table(name = "pokemon_card")
public class PokemonCard extends Card {
    @Column(name = "hp")
    private Integer hp;

    @Column(name = "energy_type")
    private String energyType;

    @Column(name = "weakness")
    private String weakness;

    public PokemonCard() {}

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hp, energyType, weakness);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PokemonCard)) return false;
        if (!super.equals(o)) return false;
        PokemonCard that = (PokemonCard) o;
        return Objects.equals(hp, that.hp) &&
                Objects.equals(energyType, that.energyType) &&
                Objects.equals(weakness, that.weakness);
    }
}