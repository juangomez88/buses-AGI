package com.buses.agi.service;

import com.buses.agi.DTO.DestinoDTO;
import java.util.Optional;

public interface DestinoService {
    Optional<DestinoDTO> findDestinoByNombre(String nombre);
    Optional<DestinoDTO> findDestinoById(Long id);
}