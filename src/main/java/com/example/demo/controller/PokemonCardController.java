package com.example.demo.controller;

import com.example.demo.entity.PokemonCard;
import com.example.demo.repository.PokemonCardRepository;
import com.example.demo.service.PokemonCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pokemon-cards")
public class PokemonCardController {

    @Autowired
    private PokemonCardRepository pokemonCardRepository;

    @GetMapping
    public List<PokemonCard> getAllPokemonCards() {
        return pokemonCardRepository.findAll(); // Ajouter cette méthode dans PokemonCardRepository si besoin
    }

    @GetMapping("/{id}")
    public PokemonCard getPokemonCardById(@PathVariable Long id) {
        return pokemonCardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carte Pokémon ID " + id + " non trouvée"));
    }
}