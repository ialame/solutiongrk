package com.example.demo.controller;

import com.example.demo.entity.YugiohSet;
import com.example.demo.repository.YugiohSetRepository;
import com.example.demo.service.YugiohDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/yugioh-sets")
public class YugiohSetController {

    @Autowired
    private YugiohDataService yugiohDataService;

    @Autowired
    private YugiohSetRepository yugiohSetRepository;

    @GetMapping
    public List<YugiohSet> getAllYugiohSets() {
        return yugiohSetRepository.findAll();
    }

    @GetMapping("/{id}")
    public YugiohSet getYugiohSetById(@PathVariable Long id) {
        return yugiohSetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Set Yu-Gi-Oh ID " + id + " non trouvé"));
    }

    @PostMapping("/process/{setCode}")
    public ResponseEntity<String> processYugiohSet(@PathVariable String setCode) {
        try {
            yugiohDataService.processYugiohSet(setCode);
            return ResponseEntity.ok("Set Yu-Gi-Oh " + setCode + " processed successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body("Failed to process set " + setCode + ": " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing set " + setCode + ": " + e.getMessage());
        }
    }
}