package com.tomas.miproyecto.clients.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultClientRestTemplateDTO {

    private long id;
    private long encuestadorId;
    private long barrioId;
    private long equipoId;
    private BigDecimal votos;


}
