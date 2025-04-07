package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "yugioh_set")
@Data
@EqualsAndHashCode(callSuper = true)
public class YugiohSet extends CardSet {
    // Champs spécifiques si besoin
}