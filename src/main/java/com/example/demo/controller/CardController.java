package com.example.demo.controller;

import com.example.demo.entity.Card;
import com.example.demo.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    @Autowired
    private CardService cardService;

    @PostMapping
    public Card createCard(@RequestBody Card card) {
        return cardService.saveCard(card);
    }

    @GetMapping("/{id}")
    public Card getCard(@PathVariable Long id) {
        return cardService.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found: " + id));
    }
}