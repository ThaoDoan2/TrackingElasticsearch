package com.example.rest_service.service.converter;

import org.springframework.stereotype.Component;

import com.example.rest_service.dto.IapDTO;
import com.example.rest_service.repository.iap.IapDocument;

@Component
public class IapDTOConverter implements IConverter<IapDocument, IapDTO> {

    @Override
    public Class<IapDocument> getDocumentClass() {
        return IapDocument.class;
    }

    @Override
    public IapDTO convertToDto(IapDocument document) {
        if (document == null) {
            return null;
        }

        IapDTO dto = new IapDTO();
        dto.setId(document.getId());
        dto.setUserId(document.getUserId());
        dto.setCountry(document.getCountry());
        dto.setGameId(document.getGameId());
        dto.setEventType(document.getEventType());
        dto.setPlacement(document.getPlacement());
        dto.setSubPlacement(document.getSubPlacement());
        dto.setPlatform(document.getPlatform());
        dto.setGameVersion(document.getGameVersion());
        dto.setLevel(document.getLevel());
        dto.setLoggedDay(document.getLoggedDay());
        dto.setAccountCreatedDate(document.getAccountCreatedDate());
        dto.setDate(document.getDate());
        dto.setProductId(document.getProductId());
        dto.setTransactionId(document.getTransactionId());
        dto.setOrderId(document.getOrderId());
        dto.setPurchaseState(document.getPurchaseState());
        dto.setReceipt(document.getReceipt());
        dto.setCurrencyCode(document.getCurrencyCode());
        dto.setPurchaseToken(document.getPurchaseToken());
        dto.setPrice(document.getPrice());
        return dto;
    }

    @Override
    public IapDocument convertToDocument(IapDTO dto) {
        if (dto == null) {
            return null;
        }

        IapDocument document = new IapDocument();
        document.setId(dto.getId());
        document.setUserId(dto.getUserId());
        document.setCountry(dto.getCountry());
        document.setGameId(dto.getGameId());
        document.setEventType(dto.getEventType());
        document.setPlacement(dto.getPlacement());
        document.setSubPlacement(dto.getSubPlacement());
        document.setPlatform(dto.getPlatform());
        document.setGameVersion(dto.getGameVersion());
        document.setLevel(dto.getLevel());
        document.setLoggedDay(dto.getLoggedDay());
        document.setAccountCreatedDate(dto.getAccountCreatedDate());
        document.setDate(dto.getDate());
        document.setProductId(dto.getProductId());
        document.setTransactionId(dto.getTransactionId());
        document.setOrderId(dto.getOrderId());
        document.setPurchaseState(dto.getPurchaseState());
        document.setReceipt(dto.getReceipt());
        document.setCurrencyCode(dto.getCurrencyCode());
        document.setPurchaseToken(dto.getPurchaseToken());
        document.setPrice(dto.getPrice());
        return document;
    }
}
