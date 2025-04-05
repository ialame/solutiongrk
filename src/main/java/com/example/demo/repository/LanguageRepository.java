package com.example.demo.repository;

import com.example.demo.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    Language findByCode(String code);
}