package com.example.demo.dto;

import lombok.Data;

@Data
public class SetTranslationDTO {
    private String name;
    private LanguageDTO language; // Changement de String à LanguageDTO
}