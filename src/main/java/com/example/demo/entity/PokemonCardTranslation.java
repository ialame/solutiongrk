package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pokemon_card_translation")
public class PokemonCardTranslation extends CardTranslation {
    @Column(name = "flavor_text")
    private String flavorText;

    public PokemonCardTranslation() {}

    public PokemonCardTranslation(Card card, Language language, String name, String description, String flavorText) {
        super(card, language, name, description);
        this.flavorText = flavorText;
    }

    public String getFlavorText() { return flavorText; }
    public void setFlavorText(String flavorText) { this.flavorText = flavorText; }
}