package com.example.demo.translation;

import com.example.demo.entity.CardSet;
import com.example.demo.entity.Language;
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
    private CardSet cardSet;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    @Column(name = "name")
    private String name;

    public CardSetTranslation() {
    }

    public CardSetTranslation(CardSet cardSet, Language language, String name) {
        this.cardSet = cardSet;
        this.language = language;
        this.name = name;
    }
}