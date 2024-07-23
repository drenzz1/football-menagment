package com.football.dev.footballapp.services;

import com.football.dev.footballapp.dto.MatchDTO;
import com.football.dev.footballapp.models.Club;
import com.football.dev.footballapp.models.Match;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface MatchService {

    void saveMatch(MatchDTO matchDto, Long roundId);
    Page<MatchDTO> retrieveMatches(Long matchId, int pageNumber, int pageSize);
    MatchDTO getMatch(Long roundId,Long matchId);
    void updateMatch(MatchDTO matchDto, Long matchId, Long roundId);
    void deleteMatch(Long matchId, Long roundId);

    void saveMatches(List<Match> matches);

    void goalScored(Long id ,Long aLong);

    void ownGoalScored(Long id, Long aLong);

    void finnishHalfTime(Long id);
    void finnishFullTime(Long id );

//    void insertMatch(MatchDTO MatchDTO);
//    Optional<MatchDTO> selectMatchById(Long id);
//    List<MatchDTO> listAllMatch();
//    void deleteMatch(Long id);
//    void updateMatch(Long id , MatchDTO matchDTO);
}
