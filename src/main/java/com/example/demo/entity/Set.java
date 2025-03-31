package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "sets")
public class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String setCode;

    @Column(nullable = false)
    private String gameType;

    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL)
    private List<SetTranslation> translations;

    @ManyToMany(mappedBy = "sets")
    private List<Card> cards;

    @ManyToOne
    @JoinColumn(name = "serie_id", nullable = false) // Rend la relation obligatoire
    private Serie serie;

    // Getters et Setters

}