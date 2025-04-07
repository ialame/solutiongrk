package com.example.demo.entity;

import com.example.demo.translation.PokemonSetTranslation;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PokemonSet extends CardSet {
    @OneToMany(mappedBy = "cardSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PokemonSetTranslation> translations = new ArrayList<>();
}