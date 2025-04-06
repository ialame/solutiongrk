package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "yugioh_set_translation")
@Data
public class YugiohSetTranslation extends CardSetTranslation {

    public YugiohSetTranslation() {
    }

    public YugiohSetTranslation(CardSet cardSet, Language language, String name) {
        super(cardSet, language, name);
    }
}