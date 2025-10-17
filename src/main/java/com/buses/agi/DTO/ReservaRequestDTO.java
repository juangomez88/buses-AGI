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
public class ReservaRequestDTO {
    private Long idDestinoOrigen;
    private Long idDestinoLlegada;
    private LocalDate fechaViaje;
    private LocalTime horaViaje;

}