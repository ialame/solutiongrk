package com.example.demo.service;

import com.example.demo.entity.Set;
import com.example.demo.repository.SetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetService {

    @Autowired
    private SetRepository setRepository;

    public Set saveSet(Set set) {
        return setRepository.save(set);
    }

    // Nouvelle méthode
    public Set getSetWithCards(Long setId) {
        return setRepository.findById(setId)
                .orElseThrow(() -> new RuntimeException("Set not found with id: " + setId));
    }
}