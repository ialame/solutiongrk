package com.example.demo;

import com.example.demo.service.PokemonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PokemonDataService pokemonDataService;

    @Autowired
    public DataInitializer(PokemonDataService pokemonDataService) {
        this.pokemonDataService = pokemonDataService;
    }

    @Override
    public void run(String... args) throws Exception {
        pokemonDataService.processPokemonSet("base1"); // Correction ici
    }
}