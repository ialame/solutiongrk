package com.example.demo.translation;

import com.example.demo.entity.Card;
import com.example.demo.entity.Language;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@Table(name = "pokemon_card_translation")
@Data
@EqualsAndHashCode(callSuper = true)
public class PokemonCardTranslation extends CardTranslation {
    // Hérite de CardTranslation avec name, description, etc.
}