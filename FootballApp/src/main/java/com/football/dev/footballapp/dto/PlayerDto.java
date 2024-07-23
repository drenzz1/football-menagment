package com.football.dev.footballapp.dto;
import com.football.dev.footballapp.models.Contract;
import com.football.dev.footballapp.models.Player;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record PlayerDto(Long id,String name,Double weight, Double height,Integer shirtNumber, String imagePath,String preferred_foot,String position,List<ContractDto> contracts) {
}

