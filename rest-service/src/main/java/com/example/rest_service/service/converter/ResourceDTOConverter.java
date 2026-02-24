package com.example.rest_service.service.converter;

import org.springframework.stereotype.Component;

import com.example.rest_service.dto.ResourceDTO;
import com.example.rest_service.repository.resource.ResourceDocument;

@Component
public class ResourceDTOConverter implements IConverter<ResourceDocument, ResourceDTO> {

    @Override
    public Class<ResourceDocument> getDocumentClass() {
        return ResourceDocument.class;
    }

    @Override
    public ResourceDTO convertToDto(ResourceDocument document) {
        if (document == null) {
            return null;
        }

        ResourceDTO dto = new ResourceDTO();
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
        dto.setPlacement(document.getPlacement());
        dto.setSubPlacement(document.getSubPlacement());
        dto.setItemId(document.getItemId());
        dto.setItemName(document.getItemName());
        dto.setAmount(document.getAmount());
        return dto;
    }

    @Override
    public ResourceDocument convertToDocument(ResourceDTO dto) {
        if (dto == null) {
            return null;
        }

        ResourceDocument document = new ResourceDocument();
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
        document.setPlacement(dto.getPlacement());
        document.setSubPlacement(dto.getSubPlacement());
        document.setItemId(dto.getItemId());
        document.setItemName(dto.getItemName());
        document.setAmount(dto.getAmount());
        return document;
    }
}
