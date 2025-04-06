package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class PokemonCard extends Card {

    @Column(name = "hp")
    private Integer hp;

    @Column(name = "energy_type")
    private String energyType;

    @Column(name = "weakness")
    private String weakness;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PokemonCardTranslation> translations = new ArrayList<>();
}