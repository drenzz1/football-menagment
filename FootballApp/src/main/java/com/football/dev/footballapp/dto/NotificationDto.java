package com.football.dev.footballapp.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
public record NotificationDto(String id , Long playerId,String description) {
}
