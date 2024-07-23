package com.football.dev.footballapp.dto;
import com.football.dev.footballapp.models.enums.InjuryStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
public record InjuryDto (Long id , String injuryType, LocalDate injuryDate, LocalDate expectedRecoveryTime, InjuryStatus injuryStatus) {
}
