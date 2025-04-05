package com.example.demo.repository;

import com.example.demo.entity.YugiohCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YuGiOhCardRepository extends JpaRepository<YugiohCard, Long> {
    List<YugiohCard> findByCardType(String cardType);
}