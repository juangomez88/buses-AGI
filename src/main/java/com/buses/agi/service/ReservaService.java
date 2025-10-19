package com.buses.agi.service;

import com.buses.agi.DTO.ReservaRequestDTO;
import com.buses.agi.DTO.ReservaResponseDTO;
import java.util.Optional;

public interface ReservaService {
    Optional<ReservaResponseDTO> crearReserva(ReservaRequestDTO reservaRequest);
}