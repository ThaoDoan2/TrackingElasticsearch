package com.example.rest_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rest_service.dto.PersonDTO;
import com.example.rest_service.search.SearchFilters;
import com.example.rest_service.service.IPersonService;

@RestController
@RequestMapping("/api/person")
public class PersonController {
    private final IPersonService personService;

    public PersonController(IPersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public void save(@RequestBody final PersonDTO person){
        personService.save(person);
    }

    @PostMapping("/search")
    public List<PersonDTO> search(@RequestBody final SearchFilters filters){
        return personService.search(filters);
    }

}
