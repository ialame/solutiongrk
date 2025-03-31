package com.example.demo.repository;

import com.example.demo.entity.YuGiOhCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YuGiOhCardRepository extends JpaRepository<YuGiOhCard, Long> {
    List<YuGiOhCard> findByCardType(String cardType);
}