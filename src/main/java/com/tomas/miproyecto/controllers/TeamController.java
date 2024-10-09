package com.tomas.miproyecto.controllers;


import com.tomas.miproyecto.dtos.NeighborhoodResponseDTO;
import com.tomas.miproyecto.dtos.TeamResponseDTO;
import com.tomas.miproyecto.services.FootballTeamServicesInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TeamController {

    @Autowired
    FootballTeamServicesInterface footballTeamServicesInterface;

    @GetMapping("/teams")
    public ResponseEntity<List<TeamResponseDTO>> getTeams() {

        return ResponseEntity.ok(footballTeamServicesInterface.getTeams());

    }

    @GetMapping("/neighborhoods")
    public ResponseEntity<List<NeighborhoodResponseDTO>> getNeighborhoods() {

        return ResponseEntity.ok(footballTeamServicesInterface.getByNeighborhood());

    }

}
