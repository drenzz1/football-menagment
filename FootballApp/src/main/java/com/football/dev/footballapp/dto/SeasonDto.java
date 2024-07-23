package com.football.dev.footballapp.dto;


import java.time.LocalDateTime;

public record SeasonDto (Long id,
   String name,
  boolean currentSeason,
   LocalDateTime start_date,
   LocalDateTime end_date,
   Integer headToHead,
   Integer numberOfStandings){


}
