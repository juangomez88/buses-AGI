package com.buses.agi.DTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PrecioDTO {
    private Long id;
    private Long idDestinoOrigen;
    private Long idDestinoLlegada;
    private BigDecimal valor;
    private String nombreDestinoOrigen;
    private String nombreDestinoLlegada;
}