package com.buses.agi.service;

import com.buses.agi.DTO.HorarioDTO;
import java.util.List;
import java.util.Optional;

public interface HorarioService {
    List<HorarioDTO> findHorariosByOrigenAndLlegada(Long idOrigen, Long idLlegada);
    Optional<HorarioDTO> findHorarioById(Long id);
}