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

    public List<Serie> getSeriesByGameType(String gameType) {
        return serieRepository.findByGameType(gameType);
    }
}
