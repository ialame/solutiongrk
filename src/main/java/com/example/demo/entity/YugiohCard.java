package com.example.demo.entity;

import com.example.demo.translation.YugiohCardTranslation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class YugiohCard extends Card {
    @Column(name = "attack")
    private Integer attack;

    @Column(name = "defense")
    private Integer defense;

    @Column(name = "type")
    private String type;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<YugiohCardTranslation> translations = new ArrayList<>();
}