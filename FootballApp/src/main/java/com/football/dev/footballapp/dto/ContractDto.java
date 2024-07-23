package com.football.dev.footballapp.dto;

import com.football.dev.footballapp.models.enums.ContractType;

import java.time.LocalDate;
public record ContractDto(Long id,LocalDate startDate,LocalDate endDate,Double salary, ContractType contractType) {

}
