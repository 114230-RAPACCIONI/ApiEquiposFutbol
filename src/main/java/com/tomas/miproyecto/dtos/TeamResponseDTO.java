package com.tomas.miproyecto.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamResponseDTO {

    private String name;
    private BigDecimal amountFans = new BigDecimal(0);
    private BigDecimal percentage = new BigDecimal(0);

}
