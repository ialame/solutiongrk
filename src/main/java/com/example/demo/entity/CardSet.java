package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "card_set")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@EqualsAndHashCode(callSuper = false, exclude = {"cards"})
public abstract class CardSet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_set_seq")
    @SequenceGenerator(name = "card_set_seq", sequenceName = "card_set_sequence", allocationSize = 1)
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

    @ManyToMany(mappedBy = "cardSets")
    @JsonIgnore
    private Set<Card> cards = new HashSet<>();
}