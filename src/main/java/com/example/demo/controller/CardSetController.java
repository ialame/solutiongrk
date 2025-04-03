package com.example.demo.controller;

import com.example.demo.entity.CardSet;
import com.example.demo.service.CardSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cardsets")
public class CardSetController {

    @Autowired
    private CardSetService cardSetService;

    @PostMapping
    public ResponseEntity<CardSet> createCardSet(
            @RequestParam String setCode,
            @RequestParam String setName,
            @RequestParam String gameType) {
        CardSet cardSet = cardSetService.createCardSet(setCode, setName, gameType);
        return ResponseEntity.ok(cardSet);
    }

    @GetMapping("/{setCode}")
    public ResponseEntity<CardSet> getCardSetByCode(@PathVariable String setCode) {
        return cardSetService.findBySetCode(setCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CardSet>> getAllCardSets() {
        List<CardSet> cardSets = cardSetService.findAll();
        return ResponseEntity.ok(cardSets);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCardSet(@PathVariable Long id) {
        cardSetService.deleteCardSet(id);
        return ResponseEntity.noContent().build();
    }
}