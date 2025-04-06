package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "pokemon_card_translation")
@Data
public class PokemonCardTranslation extends CardTranslation {

    public PokemonCardTranslation() {
        // Constructeur par défaut pour JPA
    }

    public PokemonCardTranslation(Card card, Language language, String name, String description, String flavorText) {
        super(card, language, name, description);
        this.setFlavorText(flavorText);
    }
}