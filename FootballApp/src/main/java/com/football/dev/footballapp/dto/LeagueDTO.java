package com.football.dev.footballapp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


public record LeagueDTO (Long id ,String name,Integer founded ,String description,String picture) {
}
