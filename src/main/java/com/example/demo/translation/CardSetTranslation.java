package com.example.demo.translation;

import com.example.demo.entity.CardSet;
import com.example.demo.entity.Language;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class CardSetTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_set_id")
    @JsonIgnore
    private CardSet cardSet;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}