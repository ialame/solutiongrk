package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.*;


@Entity
@Data
public class CardSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "set_code")
    private String setCode;

    @ManyToMany(mappedBy = "cardSets")
    @JsonIgnore
    private Set<Card> cards = new HashSet<>();
}