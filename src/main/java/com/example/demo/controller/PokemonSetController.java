package com.example.demo.controller;

import com.example.demo.entity.PokemonSet;
import com.example.demo.repository.PokemonSetRepository;
import com.example.demo.service.PokemonCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pokemon-sets")
public class PokemonSetController {

    @Autowired
    private PokemonCardService pokemonCardService;

    @Autowired
    private PokemonSetRepository pokemonSetRepository; // Ajout direct du repository pour simplicité

    @GetMapping
    public List<PokemonSet> getAllPokemonSets() {
        return pokemonSetRepository.findAll();
    }

    @GetMapping("/{id}")
    public PokemonSet getPokemonSetById(@PathVariable Long id) {
        return pokemonSetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Set Pokémon ID " + id + " non trouvé"));
    }
}