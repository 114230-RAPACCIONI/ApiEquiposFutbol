package com.tomas.miproyecto.services;

import com.tomas.miproyecto.dtos.NeighborhoodResponseDTO;
import com.tomas.miproyecto.dtos.TeamResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FootballTeamServicesInterface {

    public List<TeamResponseDTO> getTeams();

    public List<NeighborhoodResponseDTO> getByNeighborhood();
}
