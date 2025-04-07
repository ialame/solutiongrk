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
        super(card, language, name, description);
    }

    public YugiohCardTranslation(Card card, Language language, String name, String description, String flavorText) {
        super(card, language, name, description);
        this.setFlavorText(flavorText);
    }
    
}