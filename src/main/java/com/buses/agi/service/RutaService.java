package com.buses.agi.service;

import com.buses.agi.DTO.DestinoDTO;
import java.util.List;

public interface RutaService {
    List<DestinoDTO> findDestinosByOrigen(Long origenId);
}
