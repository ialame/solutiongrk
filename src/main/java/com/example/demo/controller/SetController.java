package com.example.demo.controller;

import com.example.demo.entity.Set;
import com.example.demo.service.SetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sets")
public class SetController {

    @Autowired
    private SetService setService;

    @GetMapping("/{setId}/cards")
    public Set getCardsBySet(@PathVariable Long setId) {
        return setService.getSetWithCards(setId);
    }
}