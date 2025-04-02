package com.example.demo.controller;

import com.example.demo.entity.Language;
import com.example.demo.dto.CardDTO;
import com.example.demo.dto.PokemonCardDTO;
import com.example.demo.dto.YuGiOhCardDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.SetRepository;
import com.example.demo.service.CardService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private SetRepository setRepository;

    @PostMapping
    public Card createCard(@Valid @RequestBody CardDTO cardDTO) {
        Card card;
        if (cardDTO instanceof PokemonCardDTO pokemonDTO) {
            card = new PokemonCard();
            ((PokemonCard) card).setEnergyType(pokemonDTO.getEnergyType());
            ((PokemonCard) card).setHp(pokemonDTO.getHp());
            ((PokemonCard) card).setWeakness(pokemonDTO.getWeakness());
        } else if (cardDTO instanceof YuGiOhCardDTO yuGiOhDTO) {
            card = new YuGiOhCard();
            ((YuGiOhCard) card).setLevel(yuGiOhDTO.getLevel());
            ((YuGiOhCard) card).setAttack(yuGiOhDTO.getAttack());
            ((YuGiOhCard) card).setDefense(yuGiOhDTO.getDefense());
            ((YuGiOhCard) card).setCardType(yuGiOhDTO.getCardType());
        } else {
            throw new IllegalArgumentException("Type de carte inconnu");
        }

        card.setCardNumber(cardDTO.getCardNumber());
        card.setRarity(cardDTO.getRarity());

        List<CardTranslation> translations = cardDTO.getTranslations().stream()
                .map(dto -> {
                    CardTranslation translation = new CardTranslation();
                    translation.setCard(card);
                    translation.setLanguage(Language.valueOf(dto.getLanguage()));
                    translation.setName(dto.getName());
                    translation.setDescription(dto.getDescription());
                    return translation;
                }).collect(Collectors.toList());
        card.setTranslations(translations);

        List<Set> sets = setRepository.findAllById(cardDTO.getSetIds());
        card.setSets(sets);

        return cardService.saveCard(card);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getCardImage(@PathVariable Long id) throws Exception {
        Card card = cardService.getCardById(id);
        Path filePath = Paths.get("src/main/resources/static" + card.getImagePath());
        Resource resource = new UrlResource(filePath.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }

    // Autres méthodes inchangées...
}