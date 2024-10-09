package com.tomas.miproyecto.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamInnerNeighborhoodResponseDTO {

    private String name;
    private BigDecimal totalAmountFans;
    private BigDecimal percentage;
}
