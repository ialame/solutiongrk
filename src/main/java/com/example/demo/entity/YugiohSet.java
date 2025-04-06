package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class YugiohSet extends CardSet {

    @OneToMany(mappedBy = "cardSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<YugiohSetTranslation> translations = new ArrayList<>();

    public YugiohSet() {}

    public void addTranslation(YugiohSetTranslation translation) {
        translations.add(translation);
        translation.setCardSet(this);
    }
}