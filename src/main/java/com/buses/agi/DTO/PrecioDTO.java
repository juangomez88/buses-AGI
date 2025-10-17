package com.buses.agi.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrecioDTO {
    private Long id;
    private Long idDestinoOrigen;
    private String nombreDestinoOrigen;
    private Long idDestinoLlegada;
    private String nombreDestinoLlegada;
    private BigDecimal valor;
}