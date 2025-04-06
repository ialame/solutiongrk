package com.example.demo.translation;

import com.example.demo.entity.Language;
import com.example.demo.entity.Serie;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "serie_translation")
@Data
public class SerieTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "serie_id")
    private Serie serie;

    @ManyToOne // Correction ici
    @JoinColumn(name = "language_id", nullable = false) // Colonne de jointure vers la table language
    private Language language;

    @Column(name = "name")
    private String name;
}