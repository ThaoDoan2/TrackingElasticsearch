package com.example.rest_service.service;

import java.util.List;

import com.example.rest_service.dto.GamePlayDTO;
import com.example.rest_service.dto.GamePlayLoseByLevelDTO;
import com.example.rest_service.dto.GamePlayStartByLevelDTO;
import com.example.rest_service.dto.GamePlayWinByLevelDTO;
import com.example.rest_service.repository.gameplay.GamePlayDocument;
import com.example.rest_service.search.SearchFilters;

public interface IGamePlayService {

    void save(GamePlayDocument gameplay);

    void save(GamePlayDTO gameplay);

    List<GamePlayDTO> search(SearchFilters filters);

    List<GamePlayWinByLevelDTO> totalWinsByLevel(SearchFilters filters);

    List<GamePlayStartByLevelDTO> totalStartsByLevel(SearchFilters filters);

    List<GamePlayLoseByLevelDTO> totalLosesByLevel(SearchFilters filters);

    void initData();
}
