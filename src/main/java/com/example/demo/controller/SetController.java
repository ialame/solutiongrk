package com.example.demo.controller;

import com.example.demo.Language;
import com.example.demo.dto.SetDTO;
import com.example.demo.entity.Serie;
import com.example.demo.entity.Set;
import com.example.demo.entity.SetTranslation;
import com.example.demo.repository.SerieRepository;
import com.example.demo.service.SetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sets")
public class SetController {

    @Autowired
    private SetService setService;

    @Autowired
    private SerieRepository serieRepository;

    @PostMapping
    public Set createSet(@Valid @RequestBody SetDTO setDTO) {
        Serie serie = serieRepository.findById(setDTO.getSerieId())
                .orElseThrow(() -> new IllegalArgumentException("Série introuvable avec l'ID : " + setDTO.getSerieId()));

        Set set = new Set();
        set.setSetCode(setDTO.getSetCode());
        set.setGameType(setDTO.getGameType());
        set.setSerie(serie);

        List<SetTranslation> translations = setDTO.getTranslations().stream()
                .map(dto -> {
                    SetTranslation translation = new SetTranslation();
                    translation.setSet(set);
                    translation.setLanguage(Language.valueOf(dto.getLanguage()));
                    translation.setName(dto.getName());
                    return translation;
                }).collect(Collectors.toList());
        set.setTranslations(translations);

        return setService.saveSet(set);
    }

    @GetMapping("/{gameType}")
    public List<Set> getSetsByGameType(@PathVariable String gameType) {
        return setService.getSetsByGameType(gameType);
    }
}