package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "sets")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "set_code", nullable = false)
    private String setCode;

    @ManyToOne
    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;

    @ManyToMany(mappedBy = "sets")
    private List<Card> cards = new ArrayList<>();

    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SetTranslation> translations = new ArrayList<>();

    // Getters et setters
   public void addTranslation(SetTranslation translation) { this.translations.add(translation); }
}