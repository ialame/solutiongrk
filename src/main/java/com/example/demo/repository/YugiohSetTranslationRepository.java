package com.example.demo.repository;

import com.example.demo.translation.YugiohSetTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YugiohSetTranslationRepository extends JpaRepository<YugiohSetTranslation, Long> {
    List<YugiohSetTranslation> findByCardSetId(Long cardSetId);
}