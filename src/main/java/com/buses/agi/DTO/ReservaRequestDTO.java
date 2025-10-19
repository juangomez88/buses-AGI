package com.buses.agi.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservaRequestDTO {
    private Long idDestinoOrigen;
    private Long idDestinoLlegada;
    private LocalDate fechaViaje;
    private LocalTime horaViaje;
}