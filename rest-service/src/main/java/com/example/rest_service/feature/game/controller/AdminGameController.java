package com.example.rest_service.feature.game.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rest_service.feature.game.dto.CreateGameRequest;
import com.example.rest_service.feature.game.dto.GameResponse;
import com.example.rest_service.feature.game.dto.UpdateGameRequest;
import com.example.rest_service.feature.game.service.GameService;

@RestController
@RequestMapping("/api/admin/games")
public class AdminGameController {

    private final GameService gameService;

    public AdminGameController(final GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<GameResponse> getAllGames() {
        return gameService.getAllGames();
    }

    @PostMapping
    public GameResponse createGame(@RequestBody final CreateGameRequest request) {
        return gameService.createGame(request);
    }

    @PutMapping("/{gameId}")
    public GameResponse updateGame(
            @PathVariable final String gameId,
            @RequestBody final UpdateGameRequest request) {
        return gameService.updateGame(gameId, request);
    }

    @DeleteMapping("/{gameId}")
    public void deleteGame(@PathVariable final String gameId) {
        gameService.deleteGame(gameId);
    }
}
