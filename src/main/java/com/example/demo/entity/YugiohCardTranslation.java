package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "yugioh_card_translation")
@Data
public class YugiohCardTranslation extends CardTranslation {

    public YugiohCardTranslation(Card card, Language language, String name, String description) {
        super(card, language, name, description);
        // Pas de champs spécifiques à Yu-Gi-Oh pour l'instant
    }

    // Optionnel : Constructeur complet avec flavorText si tu veux l'utiliser
    public YugiohCardTranslation(Card card, Language language, String name, String description, String flavorText) {
        super(card, language, name, description);
        this.setFlavorText(flavorText);
    }

    public YugiohCardTranslation() {

    }
}