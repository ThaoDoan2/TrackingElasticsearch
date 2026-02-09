package com.example.rest_service.service.converter;

import com.example.rest_service.dto.BaseDTO;
import com.example.rest_service.repository.AbstractDocument;

public interface IConverter<E extends AbstractDocument, T extends BaseDTO> {
    Class<E> getDocumentClass();

    T convertToDto(E document);

    E convertToDocument(T dto);
}
