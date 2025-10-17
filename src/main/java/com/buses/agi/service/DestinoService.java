package com.buses.agi.service;

import com.buses.agi.DTO.DestinoDTO;
import java.util.List;
import java.util.Optional;

public interface DestinoService {
    List<DestinoDTO> findAllDestinos();
    Optional<DestinoDTO> findDestinoById(Long id);
    Optional<DestinoDTO> findDestinoByNombre(String nombre);

}