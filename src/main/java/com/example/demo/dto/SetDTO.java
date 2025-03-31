package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

import jakarta.validation.constraints.NotNull;

@Data
public class SetDTO {
    private String setCode;
    private String gameType;
    private List<SetTranslationDTO> translations;

    @NotNull(message = "L'ID de la série est requis")
    private Long serieId;

    // Getters et Setters

}