package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "card_set")
public class CardSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne
    @JoinColumn(name = "set_id", nullable = false)
    private Set set;

    // Getters et Setters
}
