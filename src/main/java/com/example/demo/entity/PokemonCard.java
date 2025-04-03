package com.example.demo.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "pokemon_card")
public class PokemonCard extends Card {

    @Column
    private String energyType;

    @Column
    private Integer hp;

    @Column
    private String weakness;

    public PokemonCard() {
        setGameType("Pokemon");
    }

    // Getters et setters
    public String getEnergyType() { return energyType; }
    public void setEnergyType(String energyType) { this.energyType = energyType; }
    public Integer getHp() { return hp; }
    public void setHp(Integer hp) { this.hp = hp; }
    public String getWeakness() { return weakness; }
    public void setWeakness(String weakness) { this.weakness = weakness; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PokemonCard)) return false;
        if (!super.equals(o)) return false;
        PokemonCard that = (PokemonCard) o;
        return Objects.equals(energyType, that.energyType) &&
                Objects.equals(hp, that.hp) &&
                Objects.equals(weakness, that.weakness);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), energyType, hp, weakness);
    }
}