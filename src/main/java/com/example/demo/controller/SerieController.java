package com.example.demo.controller;

import com.example.demo.dto.SerieDTO;
import com.example.demo.entity.Serie;
import com.example.demo.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/series")
public class SerieController {

    @Autowired
    private SerieService serieService;

    @PostMapping
    public Serie createSerie(@RequestBody SerieDTO serieDTO) {
        Serie serie = new Serie();
        serie.setName(serieDTO.getName());
        serie.setGameType(serieDTO.getGameType());
        return serieService.saveSerie(serie);
    }

    @GetMapping("/{gameType}")
    public List<Serie> getSeriesByGameType(@PathVariable String gameType) {
        return serieService.getSeriesByGameType(gameType);
    }
}
