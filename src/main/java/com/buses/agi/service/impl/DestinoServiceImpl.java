package com.buses.agi.service.impl;

import com.buses.agi.DTO.DestinoDTO;
import com.buses.agi.model.Destino;
import com.buses.agi.repository.DestinoRepository;
import com.buses.agi.service.DestinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DestinoServiceImpl implements DestinoService {

    private final DestinoRepository destinoRepository;

    @Autowired
    public DestinoServiceImpl(DestinoRepository destinoRepository) {
        this.destinoRepository = destinoRepository;
    }

    @Override
    public List<DestinoDTO> findAllDestinos() {
        // Convierte una lista de entidades Destino a una lista de DestinoDTO
        return destinoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DestinoDTO> findDestinoById(Long id) {
        // Busca un destino por ID y lo convierte a DTO si existe
        return destinoRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public Optional<DestinoDTO> findDestinoByNombre(String nombre) {
        // Busca un destino por nombre y lo convierte a DTO si existe
        return destinoRepository.findByNombre(nombre)
                .map(this::convertToDto);
    }

    // Método auxiliar para convertir una entidad Destino a DestinoDTO
    private DestinoDTO convertToDto(Destino destino) {
        return new DestinoDTO(destino.getId(), destino.getNombre());
    }
}