package com.example.demo.translation;

import com.example.demo.entity.Card;
import com.example.demo.entity.Language;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "yugioh_card_translation")
@Data
@EqualsAndHashCode(callSuper = true)
public class YugiohCardTranslation extends CardTranslation {
    public YugiohCardTranslation() {
    }

    public YugiohCardTranslation(Card card, Language language, String name, String description) {
        super(card, language, null, null); // Ne pas passer name/description à super
        this.card = card;
        this.language = language;
        this.name = name; // Forcer ici
        this.description = description; // Forcer ici
    }

    public YugiohCardTranslation(Card card, Language language, String name, String description, String flavorText) {
        this(card, language, name, description);
        this.setFlavorText(flavorText);
    }

    @Column(name = "description", length = 1000)
    private String description;
}