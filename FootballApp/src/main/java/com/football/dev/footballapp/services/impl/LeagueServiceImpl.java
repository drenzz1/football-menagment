package com.football.dev.footballapp.services.impl;

import com.football.dev.footballapp.dto.LeagueDTO;
import com.football.dev.footballapp.exceptions.ResourceNotFoundException;
import com.football.dev.footballapp.mapper.LeagueDTOMapper;
import com.football.dev.footballapp.mapper.SeasonDtoMapper;
import com.football.dev.footballapp.models.League;
import com.football.dev.footballapp.repository.jparepository.LeagueRepository;
import com.football.dev.footballapp.repository.jparepository.SeasonRepository;
import com.football.dev.footballapp.services.LeagueService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class LeagueServiceImpl implements LeagueService {
    private final LeagueRepository leagueRepository;
    private final SeasonRepository seasonRepository;
    private final SeasonDtoMapper seasonDtoMapper;
    private final FileUploadServiceImpl fileUploadService;

    private final LeagueDTOMapper leagueDTOMapper;

  public LeagueServiceImpl(LeagueRepository leagueRepository, SeasonRepository seasonRepository, SeasonDtoMapper seasonDtoMapper, FileUploadServiceImpl fileUploadService, LeagueDTOMapper leagueDTOMapper) {
    this.leagueRepository = leagueRepository;
    this.seasonRepository = seasonRepository;
    this.seasonDtoMapper = seasonDtoMapper;
    this.fileUploadService = fileUploadService;
    this.leagueDTOMapper = leagueDTOMapper;
  }

  private static final Logger logger = LoggerFactory.getLogger(LeagueService.class);

    @Override
    public void insertLeague(LeagueDTO leagueDTO) {

        League league = leagueRepository.save(new League(leagueDTO.name(),leagueDTO.founded(),leagueDTO.description(),leagueDTO.picture()));

        // Generate a unique ID for the Elasticsearch document
        String esId = UUID.randomUUID().toString();

    }


    @Override
    public Optional<LeagueDTO> selectLeagueByName(String name) {
        return leagueRepository.findAll().stream()
                .filter(l -> l.getName().equals(name))
                .map(leagueDTOMapper::apply)
                .findAny();
    }

    @Override
    public Optional<LeagueDTO> selectLeagueById(Long id) {
        return leagueRepository.findById(id)
                .map(leagueDTOMapper::apply);
    }

    @Override
    public Page<LeagueDTO> listAllLeagues(int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<League> leaguePage = leagueRepository.findAll(pageRequest);
        return leaguePage.map(leagueDTOMapper::apply);
    }

    @Override
    public void deleteLeague(Long id) {
        League league = leagueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + id));

        // Mark the League entity as deleted
        league.setIsDeleted(true);
        league.setName(league.getName() + " - " + league.getId());
        leagueRepository.save(league);

        // Find the corresponding LeagueES document by dbId
    }


    @Override
    public void updateLeague(Long id, LeagueDTO leagueDTO) {

        leagueRepository.findById(id).ifPresent(dbLeague->{
            dbLeague.setName(leagueDTO.name());
            dbLeague.setFounded(leagueDTO.founded());
            dbLeague.setDescription(leagueDTO.description());
            dbLeague.setPicture(dbLeague.getPicture());


        });
    }


//    @Override
//    public void createSeasonForLeague(Long leagueId, SeasonDto seasonDto) throws ResourceNotFoundException {
//        League league = leagueRepository.findById(leagueId)
//                .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + leagueId));
//        Season season = new Season();
//        season.setName(seasonDto.getName());
//        season.setLeague(league);
//        seasonRepository.save(season);
//        league.getSeasons().add(season);
//        leagueRepository.save(league);
//    }

//    @Override
//    public List<SeasonDto> getSeasonsForLeague(Long leagueId) throws ResourceNotFoundException {
//        League league = leagueRepository.findById(leagueId)
//                .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + leagueId));
//
//      return null;
//    }

  @Override
  public void insertLeague(MultipartFile file, LeagueDTO leagueDTO1) {
    League league = new League(leagueDTO1.name(),leagueDTO1.founded(),leagueDTO1.description(),leagueDTO1.picture());
    String picturePath = fileUploadService.uploadFile(leagueDTO1.name(), file);
    if(picturePath == null) throw new RuntimeException("Failed to upload file.");
    league.setPicture(picturePath);
    leagueRepository.save(league);
  }

  @Override
  public void updateLeague(LeagueDTO leagueDtoMapper, Long id, MultipartFile file) {
    if(leagueDtoMapper == null){
      return;
    }
    League league = leagueRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("League not found with id: " + id));

    if (file != null && !file.isEmpty()){
      fileUploadService.deleteFile(league.getPicture());
      String fileUpload = fileUploadService.uploadFile(leagueDtoMapper.name(), file);
      if (fileUpload==null){
        throw new RuntimeException("Failed to upload file.");
      }
      league.setPicture(fileUpload);

    }
    league.setFounded(leagueDtoMapper.founded());
    league.setDescription(leagueDtoMapper.description());
    league.setName(leagueDtoMapper.name());

    leagueRepository.save(league);
  }




}
