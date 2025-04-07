package com.example.demo.translation;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "yugioh_set_translation")
@Data
@EqualsAndHashCode(callSuper = true)
public class YugiohSetTranslation extends CardSetTranslation {
    // Champs spécifiques si besoin
}