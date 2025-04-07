package com.example.demo;

import com.example.demo.service.PokemonDataService;
import com.example.demo.service.YugiohDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PokemonDataService pokemonDataService;
    private final YugiohDataService yugiohDataService;

    @Autowired
    public DataInitializer(PokemonDataService pokemonDataService, YugiohDataService yugiohDataService) {
        this.pokemonDataService = pokemonDataService;
        this.yugiohDataService = yugiohDataService;
    }

    @Override
    public void run(String... args) throws Exception {
        //pokemonDataService.processPokemonSet("base1"); // Correction ici
        //yugiohDataService.processYugiohSet("LOB");
    }
}