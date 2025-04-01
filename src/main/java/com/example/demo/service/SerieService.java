package com.example.demo.service;

import com.example.demo.entity.Serie;
import com.example.demo.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    public List<Serie> getSeriesByGameType(String gameType) {
        return serieRepository.findByGameType(gameType);
    }

    public Serie saveSerie(Serie serie) {
        return serieRepository.save(serie);
    }

    public Optional<Serie> findById(Long id) {
        return serieRepository.findById(id);
    }
}