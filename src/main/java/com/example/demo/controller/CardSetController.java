package com.example.demo.controller;

import com.example.demo.entity.CardSet;
import com.example.demo.service.CardSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cardsets")
public class CardSetController {
    @Autowired
    private CardSetService cardSetService;

    @PostMapping
    public CardSet createCardSet(@RequestBody CardSetRequest request) {
        return cardSetService.createCardSet(
                request.getSetCode(), request.getName(), request.getSerieName(),
                "Pokemon", null, null, null, null
        );
    }

    @GetMapping("/{setCode}")
    public CardSet getCardSet(@PathVariable String setCode) {
        return cardSetService.findBySetCode(setCode)
                .orElseThrow(() -> new RuntimeException("CardSet not found: " + setCode));
    }

    @GetMapping
    public List<CardSet> getAllCardSets() {
        return cardSetService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteCardSet(@PathVariable Long id) {
        cardSetService.deleteCardSet(id);
    }

    public static class CardSetRequest {
        private String setCode;
        private String name;
        private String serieName;

        public String getSetCode() { return setCode; }
        public void setSetCode(String setCode) { this.setCode = setCode; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSerieName() { return serieName; }
        public void setSerieName(String serieName) { this.serieName = serieName; }
    }
}