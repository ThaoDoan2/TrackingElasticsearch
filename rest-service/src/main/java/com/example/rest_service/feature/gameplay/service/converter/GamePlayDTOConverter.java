package com.example.rest_service.feature.gameplay.service.converter;

import org.springframework.stereotype.Component;

import com.example.rest_service.feature.gameplay.dto.GamePlayDTO;
import com.example.rest_service.feature.gameplay.repository.GamePlayDocument;
import com.example.rest_service.service.converter.IConverter;

@Component
public class GamePlayDTOConverter implements IConverter<GamePlayDocument, GamePlayDTO> {

    @Override
    public Class<GamePlayDocument> getDocumentClass() {
        return GamePlayDocument.class;
    }

    @Override
    public GamePlayDTO convertToDto(GamePlayDocument document) {
        if (document == null) {
            return null;
        }

        GamePlayDTO dto = new GamePlayDTO();
        dto.setId(document.getId());
        dto.setUserId(document.getUserId());
        dto.setGameId(document.getGameId());
        dto.setEventType(document.getEventType());
        dto.setPlatform(document.getPlatform());
        dto.setCountry(document.getCountry());
        dto.setGameVersion(document.getGameVersion());
        dto.setHighestLevel(document.getHighestLevel());
        dto.setLoggedDay(document.getLoggedDay());
        dto.setAccountCreatedDate(document.getAccountCreatedDate());
        dto.setDate(document.getDate());
        dto.setDuration(document.getDuration());
        dto.setGameMode(document.getGameMode());
        dto.setGameLevel(document.getGameLevel());
        dto.setDifficulty(document.getDifficulty());
        dto.setStatus(document.getStatus());
        dto.setCompletion(document.getCompletion());
        return dto;
    }

    @Override
    public GamePlayDocument convertToDocument(GamePlayDTO dto) {
        if (dto == null) {
            return null;
        }

        GamePlayDocument document = new GamePlayDocument();
        document.setId(dto.getId());
        document.setUserId(dto.getUserId());
        document.setGameId(dto.getGameId());
        document.setEventType(dto.getEventType());
        document.setPlatform(dto.getPlatform());
        document.setCountry(dto.getCountry());
        document.setGameVersion(dto.getGameVersion());
        document.setHighestLevel(dto.getHighestLevel());
        document.setLoggedDay(dto.getLoggedDay());
        document.setAccountCreatedDate(dto.getAccountCreatedDate());
        document.setDate(dto.getDate());
        document.setDuration(dto.getDuration());
        document.setGameMode(dto.getGameMode());
        document.setGameLevel(dto.getGameLevel());
        document.setDifficulty(dto.getDifficulty());
        document.setStatus(dto.getStatus());
        document.setCompletion(dto.getCompletion());
        return document;
    }
}
