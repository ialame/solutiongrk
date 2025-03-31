package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class InitializationController {

    @Autowired
    private DataInitializer dataInitializer;

    @PostMapping("/initialize")
    public ResponseEntity<String> initializeData() throws Exception {
        dataInitializer.run(); // Appelle la logique d’initialisation
        return ResponseEntity.ok("Initialisation terminée");
    }
}
