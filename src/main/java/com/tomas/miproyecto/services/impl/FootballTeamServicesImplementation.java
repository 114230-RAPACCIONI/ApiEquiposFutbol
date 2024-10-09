package com.tomas.miproyecto.services.impl;


import com.tomas.miproyecto.clients.NeighborhoodClientRestTemplate;
import com.tomas.miproyecto.clients.ResultClientRestTemplate;
import com.tomas.miproyecto.clients.TeamClientRestTemplate;
import com.tomas.miproyecto.clients.dtos.NeighborhoodClientDTO;
import com.tomas.miproyecto.clients.dtos.ResultClientRestTemplateDTO;
import com.tomas.miproyecto.dtos.NeighborhoodResponseDTO;
import com.tomas.miproyecto.dtos.NeighborhoodWithTeam;
import com.tomas.miproyecto.dtos.TeamInnerNeighborhoodResponseDTO;
import com.tomas.miproyecto.dtos.TeamResponseDTO;
import com.tomas.miproyecto.services.FootballTeamServicesInterface;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class FootballTeamServicesImplementation implements FootballTeamServicesInterface {


    @Autowired
    TeamClientRestTemplate teamClientRestTemplate;

    @Autowired
    ResultClientRestTemplate resultClientRestTemplate;

    @Autowired
    NeighborhoodClientRestTemplate neighborhoodClientRestTemplate;

    @Autowired
    ModelMapper modelMapper;

    public List<TeamResponseDTO> getTeams() {
        Map<Long, TeamResponseDTO> teamResponseDTOMap = new HashMap<>();

        Arrays.stream(teamClientRestTemplate.getTeams().getBody()).forEach(x -> teamResponseDTOMap.put(x.getId(), modelMapper.map(x, TeamResponseDTO.class)));

        List<ResultClientRestTemplateDTO> resultClientRestTemplateList = Arrays.stream(resultClientRestTemplate.getResults().getBody()).toList();

        for (int i = 0; i < resultClientRestTemplateList.size(); i++) {
            TeamResponseDTO teamResponseDTO = teamResponseDTOMap.get(resultClientRestTemplateList.get(i).getEquipoId());

            if (teamResponseDTO != null) {
                teamResponseDTO.setAmountFans(teamResponseDTO.getAmountFans().add(resultClientRestTemplateList.get(i).getVotos()));
            }
        }
        setPercentage(teamResponseDTOMap);

        return teamResponseDTOMap.values().stream().toList();
    }


    public List<NeighborhoodResponseDTO> getByNeighborhood() {

        Map<Long, Map<Long, NeighborhoodWithTeam>> mapTeamsByNeighborhoodsMap = new HashMap<>();

        Map<Long, String> mapTeamClientDTO = new HashMap<>();

        Map<Long, String> mapNeighborhoodClientDTO = new HashMap<>();

        Arrays.stream(teamClientRestTemplate.getTeams().getBody()).forEach(x -> mapTeamClientDTO.put(x.getId(), x.getName()));

        Arrays.stream(neighborhoodClientRestTemplate.getNeighborhoods().getBody()).forEach(x -> mapNeighborhoodClientDTO.put(x.getId(), x.getNombre()));

        List<NeighborhoodClientDTO> neighborhoodClientDTOList = Arrays.stream(neighborhoodClientRestTemplate.getNeighborhoods().getBody()).toList();

        neighborhoodClientDTOList.forEach(x -> mapTeamsByNeighborhoodsMap.put(x.getId(), new HashMap<>()));

        List<ResultClientRestTemplateDTO> resultClientRestTemplateDTOList = Arrays.stream(resultClientRestTemplate.getResults().getBody()).toList();

        for (int i = 0; i < resultClientRestTemplateDTOList.size(); i++) {

            if (mapTeamsByNeighborhoodsMap.containsKey(resultClientRestTemplateDTOList.get(i).getBarrioId())) {

                Map<Long, NeighborhoodWithTeam> teamWithFansCountMap = mapTeamsByNeighborhoodsMap.get(resultClientRestTemplateDTOList.get(i).getBarrioId());

                BigDecimal amountFans = new BigDecimal(0);

                if (teamWithFansCountMap.containsKey(resultClientRestTemplateDTOList.get(i).getEquipoId())) {

                    teamWithFansCountMap.replace(resultClientRestTemplateDTOList.get(i).getEquipoId(),
                    new NeighborhoodWithTeam(
                            teamWithFansCountMap.get(resultClientRestTemplateDTOList.get(i).getEquipoId()).getTeamName(),
                            teamWithFansCountMap.get(resultClientRestTemplateDTOList.get(i).getEquipoId()).getNeighborhoodName(),
                            (teamWithFansCountMap.get(resultClientRestTemplateDTOList.get(i).getEquipoId()).getAmountFans().add(resultClientRestTemplateDTOList.get(i).getVotos()))));


                } else {
                    teamWithFansCountMap.putIfAbsent(resultClientRestTemplateDTOList.get(i).getEquipoId(),

                    new NeighborhoodWithTeam(

                            mapTeamClientDTO.get(resultClientRestTemplateDTOList.get(i).getEquipoId()),
                            mapNeighborhoodClientDTO.get(resultClientRestTemplateDTOList.get(i).getBarrioId()),
                            amountFans.add(resultClientRestTemplateDTOList.get(i).getVotos())

                    ));

                }
            }
        }


        List<NeighborhoodResponseDTO> neighborhoodResponseDTOList = new ArrayList<>();

        for (int i = 1; i <= mapTeamsByNeighborhoodsMap.size(); i++) {

            NeighborhoodResponseDTO neighborhoodResponseDTO = new NeighborhoodResponseDTO();
            Map<String, BigDecimal> teamsWithAmountFans = new HashMap<>();
            Map<Long, TeamInnerNeighborhoodResponseDTO> teamsMap = new HashMap<>();

            for (int j = 1; j <= mapTeamsByNeighborhoodsMap.get((long) i).size(); j++) {

                NeighborhoodWithTeam neighborhoodWithTeam = mapTeamsByNeighborhoodsMap.get((long) i).get((long) j);
                neighborhoodResponseDTO.setNameNeighborhood(neighborhoodWithTeam.getNeighborhoodName());
                teamsMap.put((long) j, new TeamInnerNeighborhoodResponseDTO(neighborhoodWithTeam.getTeamName(), neighborhoodWithTeam.getAmountFans(), new BigDecimal(0L)));
            }
            neighborhoodResponseDTO.setTeamsMap(teamsMap);

            neighborhoodResponseDTOList.add(neighborhoodResponseDTO);
        }

        return getPercentages(neighborhoodResponseDTOList);
    }

    public BigDecimal getTotalAmountFans(Map<Long, TeamResponseDTO> teams) {

        BigDecimal total = new BigDecimal(0);

        for (int i = 0; i < teams.size(); i++) {

            total = total.add(teams.values().stream().toList().get(i).getAmountFans());
        }
        return total;
    }

    public void setPercentage(Map<Long, TeamResponseDTO> teams) {

        BigDecimal totalAmountFans = getTotalAmountFans(teams);

        for (int i = 1; i <= teams.size(); i++) {

            TeamResponseDTO team = teams.get((long) i);

            if (team != null)

                team.setPercentage((team.getAmountFans().divide(totalAmountFans, 5, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));

            teams.replace((long) i, team);
        }

    }

    public List<NeighborhoodResponseDTO> getPercentages(List<NeighborhoodResponseDTO> neighborhoodResponseDTOList) {

        for (int i = 0; i < neighborhoodResponseDTOList.size(); i++) {

            BigDecimal total = new BigDecimal(0);

            for (int j = 1; j <= neighborhoodResponseDTOList.get(i).getTeamsMap().size(); j++) {

                TeamInnerNeighborhoodResponseDTO teamInnerNeighborhoodResponseDTO = neighborhoodResponseDTOList.get(i).getTeamsMap().get((long) j);

                total = total.add(teamInnerNeighborhoodResponseDTO.getTotalAmountFans());
            }

            for (int k = 1; k <= neighborhoodResponseDTOList.get(i).getTeamsMap().size(); k++) {

                TeamInnerNeighborhoodResponseDTO teamInnerNeighborhoodResponseDTO = neighborhoodResponseDTOList.get(i).getTeamsMap().get((long) k);

                teamInnerNeighborhoodResponseDTO.setPercentage(teamInnerNeighborhoodResponseDTO.getTotalAmountFans().divide(total, 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
            }
        }
        return neighborhoodResponseDTOList;
    }

}