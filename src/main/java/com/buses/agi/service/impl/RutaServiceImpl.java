package com.buses.agi.service.impl;

import com.buses.agi.DTO.DestinoDTO;
import com.buses.agi.model.Ruta;
import com.buses.agi.repository.RutaRepository;
import com.buses.agi.service.RutaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RutaServiceImpl implements RutaService {

    private final RutaRepository rutaRepository;

    @Override
    public List<DestinoDTO> findDestinosByOrigen(Long origenId) {
        List<Ruta> rutas = rutaRepository.findByOrigenId(origenId);

        if (rutas.isEmpty()) {
            log.warn("No hay rutas para el origen ID {}", origenId);
            return List.of();
        }

        return rutas.stream()
                .map(ruta -> {
                    var destino = ruta.getDestino();
                    return new DestinoDTO(destino.getId(), destino.getNombre(), destino.getActivo());
                })
                .collect(Collectors.toList());
    }
}
