package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "card_set_translation")
@Data
public class CardSetTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_set_id")
    private CardSet cardSet;

    @Column(name = "name")
    private String name;

    @ManyToOne // Correction ici
    @JoinColumn(name = "language_id", nullable = false) // Colonne de jointure vers la table language
    private Language language;
}