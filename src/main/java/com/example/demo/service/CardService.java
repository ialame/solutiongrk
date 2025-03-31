package com.example.demo.service;

import com.example.demo.Language;
import com.example.demo.entity.Card;
import com.example.demo.entity.PokemonCard;
import com.example.demo.entity.YuGiOhCard;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.PokemonCardRepository;
import com.example.demo.repository.YuGiOhCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private PokemonCardRepository pokemonCardRepository;

    @Autowired
    private YuGiOhCardRepository yuGiOhCardRepository;


    public Card saveCard(Card card) {
        if (card.getSets() == null || card.getSets().isEmpty()) {
            throw new IllegalArgumentException("Une carte doit être associée à au moins un Set");
        }
        return cardRepository.save(card);
    }

        // Autres méthodes inchangées

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    public List<Card> getCardsByLanguage(Language language) {
        return cardRepository.findByTranslations_Language(language);
    }

    public Card getCardById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carte non trouvée"));
    }

    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }

    // Méthodes spécifiques pour Pokémon
    public List<PokemonCard> getPokemonCardsByEnergyType(String energyType) {
        return pokemonCardRepository.findByEnergyType(energyType);
    }

    // Méthodes spécifiques pour Yu-Gi-Oh!
    public List<YuGiOhCard> getYuGiOhCardsByCardType(String cardType) {
        return yuGiOhCardRepository.findByCardType(cardType);
    }
}
