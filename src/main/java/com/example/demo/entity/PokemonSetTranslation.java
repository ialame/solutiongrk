package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "pokemon_set_translation")
@Data
public class PokemonSetTranslation extends CardSetTranslation {

    public PokemonSetTranslation() {
    }

    public PokemonSetTranslation(CardSet cardSet, Language language, String name) {
        super(cardSet, language, name);
    }
}