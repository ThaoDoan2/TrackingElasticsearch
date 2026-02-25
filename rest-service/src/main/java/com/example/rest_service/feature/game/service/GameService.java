package com.example.rest_service.feature.game.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.example.rest_service.feature.game.dto.CreateGameRequest;
import com.example.rest_service.feature.game.dto.GameResponse;
import com.example.rest_service.feature.game.dto.UpdateGameRequest;
import com.example.rest_service.feature.game.repository.GameDocument;
import com.example.rest_service.feature.game.repository.GameRepository;
import com.example.rest_service.feature.user.service.UserAccountService;

@Service
public class GameService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final GameRepository gameRepository;
    private final UserAccountService userAccountService;

    public GameService(final GameRepository gameRepository, final UserAccountService userAccountService) {
        this.gameRepository = gameRepository;
        this.userAccountService = userAccountService;
    }

    public List<GameResponse> getAllGames() {
        return toList(gameRepository.findAll()).stream()
                .map(GameService::toResponse)
                .sorted((a, b) -> a.getGameId().compareToIgnoreCase(b.getGameId()))
                .toList();
    }

    public List<GameResponse> getAccessibleGames() {
        final List<String> gameScope = userAccountService.getCurrentUserGameScopeOrEmptyForAdmin();
        final List<GameDocument> games = gameScope.isEmpty()
                ? toList(gameRepository.findAll())
                : gameRepository.findByGameIdIn(gameScope);
        return games.stream()
                .filter(game -> !Boolean.FALSE.equals(game.getEnabled()))
                .map(GameService::toResponse)
                .sorted((a, b) -> a.getGameId().compareToIgnoreCase(b.getGameId()))
                .toList();
    }

    public GameResponse createGame(final CreateGameRequest request) {
        final String gameId = normalizeRequiredText(request.getGameId(), "gameId");
        if (gameRepository.existsByGameId(gameId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game already exists: " + gameId);
        }

        final GameDocument game = new GameDocument();
        game.setId(gameId);
        game.setGameId(gameId);
        game.setName(normalizeRequiredText(request.getName(), "name"));
        game.setApiKey(StringUtils.hasText(request.getApiKey()) ? request.getApiKey().trim() : generateApiKey());
        game.setEnabled(Boolean.TRUE);
        return toResponse(gameRepository.save(game));
    }

    public GameResponse updateGame(final String gameId, final UpdateGameRequest request) {
        final GameDocument game = gameRepository.findByGameId(normalizeRequiredText(gameId, "gameId"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found: " + gameId));

        if (request.getName() != null) {
            game.setName(normalizeRequiredText(request.getName(), "name"));
        }
        if (request.getEnabled() != null) {
            game.setEnabled(request.getEnabled());
        }
        if (Boolean.TRUE.equals(request.getRegenerateApiKey())) {
            game.setApiKey(generateApiKey());
        } else if (StringUtils.hasText(request.getApiKey())) {
            game.setApiKey(request.getApiKey().trim());
        }
        return toResponse(gameRepository.save(game));
    }

    public void deleteGame(final String gameId) {
        final GameDocument game = gameRepository.findByGameId(normalizeRequiredText(gameId, "gameId"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found: " + gameId));
        gameRepository.delete(game);
    }

    private static GameResponse toResponse(final GameDocument game) {
        return new GameResponse(
                game.getGameId(),
                game.getName(),
                game.getApiKey(),
                !Boolean.FALSE.equals(game.getEnabled()));
    }

    private static String normalizeRequiredText(final String value, final String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
        final String normalized = value.trim();
        if (!StringUtils.hasText(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
        return normalized;
    }

    private static String generateApiKey() {
        final byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static List<GameDocument> toList(final Iterable<GameDocument> source) {
        final List<GameDocument> rows = new java.util.ArrayList<>();
        for (GameDocument row : source) {
            rows.add(row);
        }
        return rows;
    }
}
