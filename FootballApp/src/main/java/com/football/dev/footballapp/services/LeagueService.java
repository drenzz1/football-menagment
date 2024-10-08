package com.football.dev.footballapp.services;

import com.football.dev.footballapp.dto.LeagueDTO;
import com.football.dev.footballapp.models.League;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LeagueService {
    void insertLeague(LeagueDTO leagueDTO);
    Optional<LeagueDTO> selectLeagueByName(String name);
    Optional<LeagueDTO> selectLeagueById(Long id);
    Page<LeagueDTO> listAllLeagues(int pageNumber, int pageSize);
    void deleteLeague(Long id);
    void updateLeague(Long id , LeagueDTO leagueDTO);

    void insertLeague(MultipartFile file, LeagueDTO leagueDTO1);

   void updateLeague(LeagueDTO leagueDtoMapper, Long id, MultipartFile file);

}
