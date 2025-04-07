package com.example.demo.controller;

import com.example.demo.entity.YugiohCard;
import com.example.demo.service.YugiohCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class YugiohCardController {

    @Autowired
    private YugiohCardService yugiohCardService;

    @GetMapping("/yugioh-cards")
    public List<YugiohCard> getAllCards() {
        return yugiohCardService.findAllCards();
    }
}