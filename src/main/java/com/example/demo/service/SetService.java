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

    public List<Set> getSetsByGameType(String gameType) {
        return setRepository.findByGameType(gameType); // Ligne 21
    }

    public Set saveSet(Set set) {
        return setRepository.save(set);
    }

    // Autres méthodes si nécessaires
}