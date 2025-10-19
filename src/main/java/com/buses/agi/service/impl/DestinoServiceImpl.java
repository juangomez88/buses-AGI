package com.buses.agi.service.impl;

import com.buses.agi.DTO.DestinoDTO;
import com.buses.agi.model.Destino;
import com.buses.agi.repository.DestinoRepository;
import com.buses.agi.service.DestinoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DestinoServiceImpl implements DestinoService {

    private final DestinoRepository destinoRepository;

    @Override
    public Optional<DestinoDTO> findDestinoByNombre(String nombre) {
        try {
            Optional<Destino> destino = destinoRepository.findByNombreAndActivoTrue(nombre);
            return destino.map(this::convertToDTO);
        } catch (Exception e) {
            log.error("Error al buscar destino por nombre: {}", nombre, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<DestinoDTO> findDestinoById(Long id) {
        try {
            Optional<Destino> destino = destinoRepository.findByIdAndActivoTrue(id);
            return destino.map(this::convertToDTO);
        } catch (Exception e) {
            log.error("Error al buscar destino por ID: {}", id, e);
            return Optional.empty();
        }
    }

    private DestinoDTO convertToDTO(Destino destino) {
        DestinoDTO dto = new DestinoDTO();
        dto.setId(destino.getId());
        dto.setNombre(destino.getNombre());
        dto.setActivo(destino.getActivo());
        return dto;
    }
}