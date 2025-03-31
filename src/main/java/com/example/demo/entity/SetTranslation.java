package com.example.demo.entity;

import com.example.demo.Language;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "set_translations")
public class SetTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "set_id", nullable = false)
    private Set set;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;

    @Column(nullable = false)
    private String name;

    // Constructeur par défaut requis par JPA
    public SetTranslation() {}

    // Constructeur avec paramètres pour l’initialisation
    public SetTranslation(Set set, Language language, String name) {
        this.set = set;
        this.language = language;
        this.name = name;
    }
    // Getters et Setters
}
