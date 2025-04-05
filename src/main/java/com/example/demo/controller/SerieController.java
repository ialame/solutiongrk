package com.example.demo.controller;

import com.example.demo.entity.Serie;
import com.example.demo.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/series")
public class SerieController {

    @Autowired
    private SerieService serieService;

    @PostMapping
    public Serie createSerie(@RequestParam String gameType, @RequestParam String name) {
        return serieService.saveSerie(gameType, name); // Compatible maintenant
    }
}