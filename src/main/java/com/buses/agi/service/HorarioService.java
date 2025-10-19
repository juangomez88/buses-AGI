package com.buses.agi.service;

import com.buses.agi.DTO.HorarioDTO;
import java.util.List;

public interface HorarioService {
    List<HorarioDTO> findHorariosByOrigenAndLlegada(Long origenId, Long llegadaId);
}