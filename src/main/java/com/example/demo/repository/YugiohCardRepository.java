package com.example.demo.repository;

import com.example.demo.entity.YugiohCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YugiohCardRepository extends JpaRepository<YugiohCard, Long> {
    YugiohCard findByCardNumberAndGameType(String cardNumber, String gameType);
}