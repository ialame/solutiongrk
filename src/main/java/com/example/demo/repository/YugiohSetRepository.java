package com.example.demo.repository;

import com.example.demo.entity.YugiohSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface YugiohSetRepository extends JpaRepository<YugiohSet, Long> {
    Optional<YugiohSet> findBySetCode(String setCode);
}