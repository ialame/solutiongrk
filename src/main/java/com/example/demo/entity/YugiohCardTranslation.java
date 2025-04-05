package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "yugioh_card_translation")
public class YugiohCardTranslation extends CardTranslation {
    // Champs spécifiques à Yu-Gi-Oh! (ex. effet de carte)
    @Column(name = "effect")
    private String effect;

    // Constructeurs
    public YugiohCardTranslation() {}

    public YugiohCardTranslation(Card card, Language language, String name, String description, String effect) {
        super(card, language, name, description);
        this.effect = effect;
    }

    // Getters et setters
    public String getEffect() { return effect; }
    public void setEffect(String effect) { this.effect = effect; }
}