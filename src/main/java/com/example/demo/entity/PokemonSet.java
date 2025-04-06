package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class PokemonSet extends CardSet {

    @Column(name = "ptcgo_code")
    private String ptcgoCode;

    @Column(name = "legalities")
    private String legalities;

    @OneToMany(mappedBy = "cardSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PokemonSetTranslation> translations = new ArrayList<>();

    public PokemonSet() {}

    public void addTranslation(PokemonSetTranslation translation) {
        translations.add(translation);
        translation.setCardSet(this);
    }
}