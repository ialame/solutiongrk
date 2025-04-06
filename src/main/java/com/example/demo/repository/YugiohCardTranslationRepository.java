package com.example.demo.repository;

import com.example.demo.entity.Card;
import com.example.demo.entity.YugiohCard;
import com.example.demo.translation.YugiohCardTranslation;
import com.example.demo.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface YugiohCardTranslationRepository extends JpaRepository<YugiohCardTranslation, Long> {
    YugiohCardTranslation findByCardAndLanguage_Id(Card card, Long languageId);

}