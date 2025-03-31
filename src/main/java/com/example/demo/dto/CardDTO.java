package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PokemonCardDTO.class, name = "PokemonCard"),
        @JsonSubTypes.Type(value = YuGiOhCardDTO.class, name = "YuGiOhCard")
})
public abstract class CardDTO {
    private String cardNumber;
    private String rarity;
    private List<CardTranslationDTO> translations;

    @NotEmpty(message = "Au moins un Set doit être associé à la carte")
    private List<Long> setIds;

    // Getters et Setters
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    public List<CardTranslationDTO> getTranslations() { return translations; }
    public void setTranslations(List<CardTranslationDTO> translations) { this.translations = translations; }
    public List<Long> getSetIds() { return setIds; }
    public void setSetIds(List<Long> setIds) { this.setIds = setIds; }
}