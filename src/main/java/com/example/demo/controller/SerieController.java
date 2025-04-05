package com.example.demo.controller;

import com.example.demo.entity.Serie;
import com.example.demo.entity.SerieTranslation;
import com.example.demo.entity.Language;
import com.example.demo.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/series")
public class SerieController {
    @Autowired
    private SerieService serieService;

    @PostMapping
    public Serie createSerie(@RequestBody SerieRequest serieRequest) {
        Serie serie = serieService.saveSerie(serieRequest.getName(), serieRequest.getGameType());
        return serie;
    }

    @GetMapping("/{name}/{gameType}")
    public Serie getSerie(@PathVariable String name, @PathVariable String gameType) {
        return serieService.findByNameAndGameType(name, gameType)
                .orElseThrow(() -> new RuntimeException("Serie not found: " + name + " (" + gameType + ")"));
    }

    public static class SerieRequest {
        private String name;
        private String gameType;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getGameType() { return gameType; }
        public void setGameType(String gameType) { this.gameType = gameType; }
    }
}