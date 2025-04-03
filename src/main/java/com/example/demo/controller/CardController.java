package com.example.demo.controller;

import com.example.demo.entity.Card;
import com.example.demo.entity.CardSet;
import com.example.demo.entity.CardTranslation;
import com.example.demo.entity.PokemonCard;
import com.example.demo.repository.CardRepository;
import com.example.demo.service.EntityPersistenceService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private EntityPersistenceService persistenceService;
    @Autowired
    private CardRepository cardRepository;
    @PostMapping
    public ResponseEntity<Card> createCard(
            @RequestBody CardRequest cardRequest) {
        // Créer une carte générique (sera spécialisée par gameType)
        Card card;
        switch (cardRequest.getGameType()) {
            case "Pokemon":
                card = new PokemonCard();
                ((PokemonCard) card).setEnergyType(cardRequest.getEnergyType());
                ((PokemonCard) card).setHp(cardRequest.getHp());
                ((PokemonCard) card).setWeakness(cardRequest.getWeakness());
                break;
            // Ajouter d'autres cas pour YuGiOh, Lorcana, etc.
            default:
                throw new IllegalArgumentException("Type de jeu non supporté : " + cardRequest.getGameType());
        }

        card.setCardNumber(cardRequest.getCardNumber());
        card.setRarity(cardRequest.getRarity());
        card.setImagePath(cardRequest.getImagePath());

        // Convertir List<CardTranslation> en Set<CardTranslation>
        Set<CardTranslation> translations = new HashSet<>(cardRequest.getTranslations());
        translations.forEach(translation -> translation.setCard(card));
        card.setTranslations(translations);

        // Convertir List<CardSet> en Set<CardSet>
        Set<CardSet> sets = new HashSet<>(cardRequest.getSets());
        card.setSets(sets);

        Card savedCard = persistenceService.saveCard(card, sets.iterator().next()); // Sauvegarde avec le premier set
        return ResponseEntity.ok(savedCard);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> getCard(@PathVariable Long id) {
        return cardRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Autres méthodes selon vos besoins
}

// Classe DTO pour la requête
@Setter
@Getter
class CardRequest {
    // Getters et setters
    private String cardNumber;
    private String gameType;
    private String rarity;
    private String imagePath;
    private List<CardTranslation> translations;
    private List<CardSet> sets;
    // Propriétés spécifiques à Pokémon
    private String energyType;
    private Integer hp;
    private String weakness;

}