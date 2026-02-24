package com.example.rest_service.feature.gameplay.service;

import java.util.List;

import com.example.rest_service.feature.gameplay.dto.GamePlayDTO;
import com.example.rest_service.feature.gameplay.dto.GamePlayLoseByLevelDTO;
import com.example.rest_service.feature.gameplay.dto.GamePlayStartByLevelDTO;
import com.example.rest_service.feature.gameplay.dto.GamePlayWinByLevelDTO;
import com.example.rest_service.feature.gameplay.repository.GamePlayDocument;
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
