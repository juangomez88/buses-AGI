package com.buses.agi.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioDTO {
    private Long id;
    private LocalTime horaSalida;
    private Long idDestinoOrigen;
    private String nombreDestinoOrigen;
    private Long idDestinoLlegada;
    private String nombreDestinoLlegada;
}