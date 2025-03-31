package com.example.demo.dto;

public class YuGiOhCardDTO extends CardDTO {
    private int level;
    private int attack;
    private int defense;
    private String cardType;

    // Getters et Setters
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }
    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }
    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }
}
