
// Set.java (exemple)
package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.ArrayList;

@Data

@Entity
@Table(name = "sets")
public class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "set_code", nullable = false)
    private String setCode;

    @Column(name = "game_type", nullable = false)
    private String gameType;

    @ManyToOne
    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;

    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SetTranslation> translations = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "cards_sets",
            joinColumns = @JoinColumn(name = "set_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private List<Card> cards = new ArrayList<>();

    public Set() {
    }

    public void setTranslations(List<SetTranslation> translations) {
        this.translations.clear();
        this.translations.addAll(translations);
    }

    // Getters et Setters

}