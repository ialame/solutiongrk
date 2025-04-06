package com.example.demo.repository;

import com.example.demo.entity.YugiohCard;
import com.example.demo.translation.YugiohCardTranslation;
import com.example.demo.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YugiohCardTranslationRepository extends JpaRepository<YugiohCardTranslation, Long> {
    YugiohCardTranslation findByCardAndLanguage(YugiohCard card, Language language);
}