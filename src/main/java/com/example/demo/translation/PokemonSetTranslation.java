package com.example.demo.translation;

import com.example.demo.translation.CardSetTranslation;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "pokemon_set_translation")
@Data
@EqualsAndHashCode(callSuper = true)
public class PokemonSetTranslation extends CardSetTranslation {
    // Champs spécifiques si besoin
}