package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "card_set")
@Data
public class CardSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "set_code")
    private String setCode;

    @Column(name = "release_date")
    private Date releaseDate;

    @Column(name = "total_cards")
    private int totalCards;

    @ManyToOne
    @JoinColumn(name = "serie_id")
    private Serie serie;

    @ManyToMany
    @JoinTable(
            name = "card_card_set",
            joinColumns = @JoinColumn(name = "card_set_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    @JsonIgnore
    private Set<Card> cards = new HashSet<>();
}