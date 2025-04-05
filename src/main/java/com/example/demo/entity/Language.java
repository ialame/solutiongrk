package com.example.demo.entity;

import lombok.Getter;

@Getter
public enum Language {
    EN("en"), // Anglais
    FR("fr"), // Français
    IT("it"); // Italien

    private final String code;

    Language(String code) {
        this.code = code;
    }

}