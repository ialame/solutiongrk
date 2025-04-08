package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "yugioh_set", uniqueConstraints = @UniqueConstraint(columnNames = "set_code"))
@Data
@EqualsAndHashCode(callSuper = true)
public class YugiohSet extends CardSet {
    // Pas de champs supplémentaires nécessaires
}