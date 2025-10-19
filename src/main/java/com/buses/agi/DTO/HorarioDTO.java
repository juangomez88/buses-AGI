package com.buses.agi.DTO;

import lombok.Data;
import java.time.LocalTime;

@Data
public class HorarioDTO {
    private Long id;
    private Long idDestinoOrigen;
    private Long idDestinoLlegada;
    private LocalTime horaSalida;
    private String nombreDestinoOrigen;
    private String nombreDestinoLlegada;
}