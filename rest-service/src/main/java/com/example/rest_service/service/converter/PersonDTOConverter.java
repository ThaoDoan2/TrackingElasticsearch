package com.example.rest_service.service.converter;

import org.springframework.stereotype.Component;

import com.example.rest_service.dto.PersonDTO;
import com.example.rest_service.repository.person.PersonDocument;

@Component
public class PersonDTOConverter implements IConverter<PersonDocument, PersonDTO> {

    @Override
    public Class<PersonDocument> getDocumentClass() {
        return PersonDocument.class;
    }

    @Override
    public PersonDTO convertToDto(PersonDocument document) {
        if (document == null) {
            return null;
        }

        PersonDTO dto = new PersonDTO();
        dto.setId(document.getId());
        dto.setName(document.getName());

        return dto;
    }

    @Override
    public PersonDocument convertToDocument(PersonDTO dto) {
        if (dto == null) {
            return null;
        }

        PersonDocument document = new PersonDocument();
        document.setId(dto.getId());
        document.setName(dto.getName());
        return document;
    }
}