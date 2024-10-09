package com.tomas.miproyecto.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NeighborhoodResponseDTO {

    private String nameNeighborhood;
    private Map<Long,TeamInnerNeighborhoodResponseDTO> teamsMap = new HashMap<>();


}
