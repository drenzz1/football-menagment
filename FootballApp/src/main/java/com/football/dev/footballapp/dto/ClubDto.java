package com.football.dev.footballapp.dto;

import com.football.dev.footballapp.models.League;
import com.football.dev.footballapp.models.Stadium;
import com.football.dev.footballapp.models.UserEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

public record ClubDto( Long id,
   String name,
   Integer foundedYear,
   String city,
   String website) {


}
