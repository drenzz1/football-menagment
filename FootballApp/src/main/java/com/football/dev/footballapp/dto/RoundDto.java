package com.football.dev.footballapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public record RoundDto (Long id ,LocalDateTime start_date,LocalDateTime end_date,List<MatchDTO> matches){

}
