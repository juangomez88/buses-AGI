package com.buses.agi.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {
    private Long id;
    private String nombreDestinoOrigen;
    private String nombreDestinoLlegada;
    private LocalDate fechaViaje;
    private LocalTime horaViaje;
    private BigDecimal valorTotal;
    private String estado;
}