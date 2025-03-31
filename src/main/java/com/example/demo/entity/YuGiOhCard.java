package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "yugioh_cards")
public class YuGiOhCard extends Card {

    @Column(nullable = false)
    private int level; // Niveau (ex. 4 pour "Blue-Eyes White Dragon")

    @Column(nullable = false)
    private int attack; // Points d'attaque

    @Column(nullable = false)
    private int defense; // Points de défense

    @Column
    private String cardType; // Type de carte (ex. "Monstre", "Magie", "Piège")
    // Constructeur par défaut
    public YuGiOhCard() {}
    // Getters et Setters

}
