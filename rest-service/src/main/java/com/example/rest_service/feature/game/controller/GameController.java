package com.example.rest_service.feature.game.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rest_service.feature.game.dto.GameResponse;
import com.example.rest_service.feature.game.service.GameService;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(final GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<GameResponse> getAccessibleGames() {
        return gameService.getAccessibleGames();
    }
}
