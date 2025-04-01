package com.example.demo.controller;

import com.example.demo.Language;
import com.example.demo.entity.Serie;
import com.example.demo.entity.SerieTranslation;
import com.example.demo.service.SerieService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Data
@RestController
@RequestMapping("/api/series")
public class SerieController {

    @Autowired
    private SerieService serieService;

    @PostMapping
    public Serie createSerie(@RequestBody SerieRequest serieRequest) {
        Serie serie = new Serie();
        serie.setGameType(serieRequest.getGameType());

        // Ajouter les traductions au lieu de setName
        serie.addTranslation(Language.US, serieRequest.getNameUS());
        serie.addTranslation(Language.FR, serieRequest.getNameFR());

        return serieService.saveSerie(serie); // Suppose que saveSerie existe dans SerieService
    }

    @PutMapping("/{id}")
    public Serie updateSerie(@PathVariable Long id, @RequestBody SerieRequest serieRequest) {
        Serie serie = serieService.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie not found"));

        serie.setGameType(serieRequest.getGameType());

        // Mettre à jour les traductions
        serie.getTranslations().clear(); // Supprime les anciennes traductions
        serie.addTranslation(Language.US, serieRequest.getNameUS());
        serie.addTranslation(Language.FR, serieRequest.getNameFR());

        return serieService.saveSerie(serie);
    }
}

// Classe DTO pour la requête
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
}