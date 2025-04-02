package com.example.demo.entity;

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

    // Getters et setters

    public SetTranslation() {}
    public SetTranslation(Set set, Language language, String name) {
        this.set = set;
        this.language = language;
        this.name = name;
    }
}