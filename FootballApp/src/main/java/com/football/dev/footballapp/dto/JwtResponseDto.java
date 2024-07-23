package com.football.dev.footballapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public record JwtResponseDto (String accessToken, String refreshToken) {
}
