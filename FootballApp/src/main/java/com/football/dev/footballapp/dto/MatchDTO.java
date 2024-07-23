package com.football.dev.footballapp.dto;

import com.football.dev.footballapp.models.Club;
import com.football.dev.footballapp.models.Stadium;
import com.football.dev.footballapp.models.enums.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public record MatchDTO(Long id ,ClubDto homeTeam,ClubDto awayTeam, LocalDateTime matchDate,String result, Integer homeTeamScore,Integer awayTeamScore,MatchStatus matchStatus){
}
