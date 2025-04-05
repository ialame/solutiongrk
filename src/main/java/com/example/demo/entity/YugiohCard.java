package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "yugioh_card")
@PrimaryKeyJoinColumn(name = "id")
public class YugiohCard extends Card {

    @Column(name = "attack")
    private Integer attack;

    @Column(name = "defense")
    private Integer defense;

    @Column(name = "level")
    private Integer level;

    @Column(name = "card_type")
    private String cardType;

    // Getters et setters

}
