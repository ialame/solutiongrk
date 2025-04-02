// SerieService.java (exemple ajusté)
package com.example.demo.service;

import com.example.demo.entity.Serie;
import com.example.demo.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    public Serie saveSerie(Serie serie) {
        return serieRepository.save(serie);
    }

    public Serie findById(Long id) {
        return serieRepository.findById(id).orElseThrow(() -> new RuntimeException("Serie not found"));
    }

    public List<Serie> findByGameType(String gameType) {
        return serieRepository.findByGameType(gameType);
    }
}