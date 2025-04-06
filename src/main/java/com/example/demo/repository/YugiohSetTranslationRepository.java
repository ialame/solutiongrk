package com.example.demo.repository;

import com.example.demo.entity.YugiohSet;
import com.example.demo.translation.YugiohSetTranslation;
import com.example.demo.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YugiohSetTranslationRepository extends JpaRepository<YugiohSetTranslation, Long> {
    YugiohSetTranslation findByCardSetAndLanguage(YugiohSet cardSet, Language language);
}