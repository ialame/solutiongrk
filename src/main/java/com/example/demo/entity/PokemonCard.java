package com.example.demo.entity;

import com.example.demo.translation.PokemonCardTranslation;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pokemon_card")
@Data
@EqualsAndHashCode(callSuper = true)
public class PokemonCard extends Card {
    @Column(name = "hp")
    private Integer hp;

    @Column(name = "type")
    private String type;

    @Column(name = "energy_type")
    private String energyType;

    @Column(name = "weakness")
    private String weakness;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PokemonCardTranslation> translations = new ArrayList<>();
}