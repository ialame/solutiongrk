package com.example.demo.repository;

import com.example.demo.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("SELECT c FROM Card c JOIN c.sets s WHERE c.cardNumber = :cardNumber AND s.setCode = :setCode")
    Optional<Card> findByCardNumberAndSet(@Param("cardNumber") String cardNumber, @Param("setCode") String setCode);
}