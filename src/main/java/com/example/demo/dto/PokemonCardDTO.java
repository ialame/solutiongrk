package com.example.demo.dto;

public class PokemonCardDTO extends CardDTO {
    private String energyType;
    private int hp;
    private String weakness;

    // Getters et Setters
    public String getEnergyType() { return energyType; }
    public void setEnergyType(String energyType) { this.energyType = energyType; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public String getWeakness() { return weakness; }
    public void setWeakness(String weakness) { this.weakness = weakness; }
}
