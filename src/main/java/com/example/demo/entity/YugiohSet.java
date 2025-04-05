package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "yugioh_set")
public class YugiohSet extends CardSet {

    @Column(name = "set_type")
    private String setType; // Ex. "Booster Pack", "Structure Deck"

    @Column(name = "card_count")
    private Integer cardCount;

    @Column(name = "rarity_distribution")
    private String rarityDistribution; // Ex. "10 Ultra Rares, 20 Super Rares"

    // Getters et setters
}
