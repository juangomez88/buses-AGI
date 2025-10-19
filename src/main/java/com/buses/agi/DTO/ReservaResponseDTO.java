package com.buses.agi.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;

@Data
public class ReservaResponseDTO {
    private Long id;
    private Long idDestinoOrigen;
    private Long idDestinoLlegada;
    private String nombreDestinoOrigen;
    private String nombreDestinoLlegada;
    private LocalDate fechaViaje;
    private LocalTime horaViaje;
    private BigDecimal valorTotal;
    private String estado;
}