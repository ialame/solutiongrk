package com.example.demo;

import com.example.demo.service.PokemonDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private PokemonDataService pokemonDataService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");
        //pokemonDataService.testPersistence(); // Test de persistance
        pokemonDataService.initializeData(); // Initialisation complète
    }
}