package com.football.dev.footballapp.services.impl;
import com.football.dev.footballapp.dto.MatchDTO;
import com.football.dev.footballapp.mapper.MatchDTOMapper;
import com.football.dev.footballapp.models.*;
import com.football.dev.footballapp.models.enums.MatchStatus;
import com.football.dev.footballapp.repository.jparepository.RoundRepository;
import com.football.dev.footballapp.repository.jparepository.ClubRepository;
import com.football.dev.footballapp.repository.jparepository.MatchRepository;
import com.football.dev.footballapp.repository.jparepository.StandingRepository;
import com.football.dev.footballapp.services.MatchService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Date;

import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {
    private final MatchRepository matchRepository;
    private final ClubRepository clubRepository;
    private final RoundRepository roundRepository;
    private final MatchDTOMapper mapper;
    private final StandingRepository standingRepository;

  public MatchServiceImpl(MatchRepository matchRepository, ClubRepository clubRepository, RoundRepository roundRepository, MatchDTOMapper mapper, StandingRepository standingRepository) {
    this.matchRepository = matchRepository;
    this.clubRepository = clubRepository;
    this.roundRepository = roundRepository;
    this.mapper = mapper;
    this.standingRepository = standingRepository;
  }

  @Override
    public void saveMatch(MatchDTO matchDto, Long roundId) {
        if(matchDto == null) throw new IllegalArgumentException("matchDto cannot be null");
        Round roundDb = roundRepository.findById(roundId).orElseThrow(() -> new EntityNotFoundException("Round not found with id: " + roundId));
        Club homeTeam = clubRepository.findById(matchDto.homeTeam().id())
                .orElseThrow(() -> new EntityNotFoundException("Home team not found with id: " + matchDto.homeTeam()));
        Club awayTeam = clubRepository.findById(matchDto.awayTeam().id())
                .orElseThrow(() -> new EntityNotFoundException("Away team not found with id: " + matchDto.awayTeam()));

        Match match = new Match(homeTeam, awayTeam, matchDto.matchDate(), matchDto.result(), matchDto.homeTeamScore(), matchDto.awayTeamScore(), roundDb);

        matchRepository.save(match);

    }

    @Override
    public Page<MatchDTO> retrieveMatches(Long roundId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Match> matchPage = matchRepository.findMatchesByRoundId(roundId, pageable);
        List<MatchDTO> roundDtos = matchPage.getContent()
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
        return PageableExecutionUtils.getPage(roundDtos, matchPage.getPageable(), matchPage::getTotalPages);
    }
    @Override
    public MatchDTO getMatch(Long roundId, Long matchId) {
        Optional<Match> match = matchRepository.findByIdAndRoundId(matchId, roundId);
        if (match.isPresent()) {
            return mapper.apply(match.get());
        } else {
            throw new EntityNotFoundException("Match not found with ids: roundId: " + roundId + " matchId: " + matchId);
        }
    }

    @Override
    public void updateMatch(MatchDTO matchDTO, Long matchId,Long roundId) {
        matchRepository.findByIdAndRoundId(matchId,roundId).ifPresent(matchDb -> {
//            matchDb.setHomeTeamId(matchDTO.getHomeTeamId());
//            matchDb.setAwayTeamId(matchDTO.getAwayTeamId());
            matchDb.setMatchDate(matchDTO.matchDate());
            matchDb.setResult(matchDTO.result());
            matchDb.setHomeTeamScore(matchDTO.homeTeamScore());
            matchDb.setAwayTeamScore(matchDTO.awayTeamScore());
            matchRepository.save(matchDb);
        });
    }
    @Override
    public void deleteMatch(Long matchId, Long roundId) {
        matchRepository.findByIdAndRoundId(matchId,roundId).ifPresent(matchDb -> {
            matchDb.setIsDeleted(true);
            matchRepository.save(matchDb);
        });
    }


  @Override
  public void saveMatches(List<Match> matches) {
    matchRepository.saveAll(matches);
  }

  @Override
  public void goalScored(Long id , Long aLong) {
      Match  match = matchRepository.findById(id).get();
      Club club = clubRepository.findById(aLong).get();

      if (club.equals(match.getHomeTeamId())){
        match.setHomeTeamScore(match.getHomeTeamScore()+1);
        club.setGoals(club.getGoals()+1);



      }else {
        match.setAwayTeamScore(match.getAwayTeamScore() + 1);
        club.setGoals(club.getGoals() + 1);


      }
    matchRepository.save(match);
    clubRepository.save(club);

  }

  @Override
  public void ownGoalScored(Long id, Long aLong) {
    Match  match = matchRepository.findById(id).get();
    Club club = clubRepository.findById(aLong).get();

    if (club.equals(match.getHomeTeamId())){
      match.setAwayTeamScore(match.getAwayTeamScore()+1);
      Club club2 = match.getAwayTeamId();
      club2.setGoals(club2.getGoals()+1);
    }else {
      match.setHomeTeamScore(match.getHomeTeamScore()+1);
      Club club3=match.getHomeTeamId();
      club3.setGoals(club3.getGoals()+1);
    }
    matchRepository.save(match);
    clubRepository.save(club);


  }

  @Override
  public void finnishHalfTime(Long id) {
    Match match = matchRepository.findById(id).get();
    match.setMatchStatus(MatchStatus.HALF_TIME);
    matchRepository.save(match);
  }

  @Override
  public void finnishFullTime(Long id) {
    // Fetch match and ensure it's present
    Optional<Match> optionalMatch = matchRepository.findById(id);
    if (!optionalMatch.isPresent()) {
      throw new IllegalArgumentException("Match not found with id: " + id);
    }
    Match match = optionalMatch.get();

    Club homeTeam = match.getHomeTeamId();
    Club awayTeam = match.getAwayTeamId();

    Integer homeTeamsGoal = match.getHomeTeamScore();
    Integer awayTeamsGoal = match.getAwayTeamScore();

    Optional<Standing> optionalStanding1 = standingRepository.findById(homeTeam.getId());
    Optional<Standing> optionalStanding2 = standingRepository.findById(awayTeam.getId());
    if (!optionalStanding1.isPresent() || !optionalStanding2.isPresent()) {
      throw new IllegalArgumentException("Standing not found ");
    }
    Standing standing1 = optionalStanding1.get();
    Standing standing2 = optionalStanding2.get();

    standing1.setGoalScored(standing1.getGoalScored() + homeTeamsGoal);
    standing2.setGoalScored(standing2.getGoalScored() + awayTeamsGoal);
    standing1.setGoalConceded(standing1.getGoalConceded() + awayTeamsGoal);
    standing2.setGoalConceded(standing2.getGoalConceded() + homeTeamsGoal);
    standing1.setMatchesPlayed(standing1.getMatchesPlayed() + 1);
    standing2.setMatchesPlayed(standing2.getMatchesPlayed() + 1);

    if (homeTeamsGoal > awayTeamsGoal) {
      standing1.setWonMatches(standing1.getWonMatches() + 1);
      standing2.setLostMatches(standing2.getLostMatches() + 1);
      standing1.setPoints(standing1.getPoints()+3);
    } else if (awayTeamsGoal > homeTeamsGoal) {
      standing1.setLostMatches(standing1.getLostMatches() + 1);
      standing2.setWonMatches(standing2.getWonMatches() + 1);
      standing2.setPoints(standing2.getPoints()+3);
    } else {
      standing1.setDrawMatches(standing1.getDrawMatches() + 1);
      standing2.setDrawMatches(standing2.getDrawMatches() + 1);
      standing1.setPoints(standing1.getPoints()+1);
      standing2.setPoints(standing2.getPoints()+1);


    }

    match.setMatchStatus(MatchStatus.FULL_TIME);
    standingRepository.save(standing1);
    standingRepository.save(standing2);
    matchRepository.save(match);

  }



//    @Override
//    public void insertMatch(MatchDTO matchDTO) {
//        Club homeTeam = clubRepository.findById(matchDTO.getHomeTeamId().getId())
//                .orElseThrow(() -> new IllegalArgumentException("Home team not found"));
//        Club awayTeam = clubRepository.findById(matchDTO.getAwayTeamId().getId())
//                .orElseThrow(() -> new IllegalArgumentException("Away team not found"));
//
//        Match match = new Match(homeTeam, awayTeam, matchDTO.getMatchDate(), matchDTO.getStadium(),
//                matchDTO.getResult(), matchDTO.getHomeTeamScore(), matchDTO.getAwayTeamScore());
//        matchRepository.save(match);
//    }
//
//
//    @Override
//    public Optional<MatchDTO> selectMatchById(Long id) {
//        return matchRepository.findById(id).map(mapper);
//    }
//
//    @Override
//    public List<MatchDTO> listAllMatch() {
//        return matchRepository.findAll().stream().map(mapper).collect(Collectors.toList());
//    }
//
//    @Override
//    public void deleteMatch(Long id) {
//        Match match = matchRepository.findById(id).get();
//        match.setIsDeleted(true);
//        matchRepository.save(match);
//    }
//
//    @Override
//    public void updateMatch(Long id, MatchDTO matchDTO) {
//        matchRepository.findById(id).ifPresent(dbMatch -> {
//            Club homeTeam = clubRepository.findById(matchDTO.getHomeTeamId().getId())
//                    .orElseThrow(() -> new IllegalArgumentException("Home team not found"));
//            Club awayTeam = clubRepository.findById(matchDTO.getAwayTeamId().getId())
//                    .orElseThrow(() -> new IllegalArgumentException("Away team not found"));
//
//            dbMatch.setHomeTeamId(homeTeam);
//            dbMatch.setAwayTeamId(awayTeam);
//            dbMatch.setMatchDate(matchDTO.getMatchDate());
//            dbMatch.setStadium(matchDTO.getStadium());
//            dbMatch.setResult(matchDTO.getResult());
//            dbMatch.setHomeTeamScore(matchDTO.getHomeTeamScore());
//            dbMatch.setAwayTeamScore(matchDTO.getAwayTeamScore());
//            matchRepository.save(dbMatch);
//        });
//    }


}
