package com.example.demo.controller;

import com.example.demo.entity.Language;
import com.example.demo.entity.Serie;
import com.example.demo.service.SerieService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.*;

@Data
@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService serieService;

    @PostMapping
    public Serie createSerie(@RequestBody Serie serie) {
        // Exemple d'ajout de traductions
        Serie newSerie = new Serie();
        newSerie.setGameType(serie.getGameType());
        newSerie.addTranslation(new SerieTranslation(newSerie, Language.US, "Base"));
        newSerie.addTranslation(new SerieTranslation(newSerie, Language.FR, "Base"));
        return serieService.saveSerie(newSerie);
    }

    @PutMapping("/{id}")
    public Serie updateSerie(@PathVariable Long id, @RequestBody Serie serie) {
        Serie existingSerie = serieService.findById(id);
        existingSerie.setGameType(serie.getGameType());
        existingSerie.getTranslations().clear();
        existingSerie.addTranslation(new SerieTranslation(existingSerie, Language.US, "Base Updated"));
        existingSerie.addTranslation(new SerieTranslation(existingSerie, Language.FR, "Base Mise à Jour"));
        return serieService.saveSerie(existingSerie);
    }
}
// Classe DTO pour la requête
/*
class SerieRequest {
    private String gameType;
    private String nameUS;
    private String nameFR;

    // Getters et Setters
// Getters et Setters
    public String getGameType() { return gameType; }
    public void setGameType(String gameType) { this.gameType = gameType; }
    public String getNameUS() { return nameUS; }
    public void setNameUS(String nameUS) { this.nameUS = nameUS; }
    public String getNameFR() { return nameFR; }
    public void setNameFR(String nameFR) { this.nameFR = nameFR; }
}*/
