package com.buses.agi.service;

import com.buses.agi.DTO.PrecioDTO;
import java.util.Optional;

public interface PrecioService {
    Optional<PrecioDTO> findPrecioByOrigenAndLlegada(Long idOrigen, Long idLlegada);
}