package com.buses.agi.service.impl;

import com.buses.agi.DTO.PrecioDTO;
import com.buses.agi.model.Precio;
import com.buses.agi.repository.PrecioRepository;
import com.buses.agi.service.PrecioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrecioServiceImpl implements PrecioService {

    private final PrecioRepository precioRepository;

    @Override
    public Optional<PrecioDTO> findPrecioByOrigenAndLlegada(Long origenId, Long llegadaId) {
        try {
            Optional<Precio> precio = precioRepository.findByOrigenAndLlegada(origenId, llegadaId);
            return precio.map(this::convertToDTO);
        } catch (Exception e) {
            log.error("Error al buscar precio para origen {} y llegada {}", origenId, llegadaId, e);
            return Optional.empty();
        }
    }

    private PrecioDTO convertToDTO(Precio precio) {
        PrecioDTO dto = new PrecioDTO();
        dto.setId(precio.getId());
        dto.setIdDestinoOrigen(precio.getDestinoOrigen().getId());
        dto.setIdDestinoLlegada(precio.getDestinoLlegada().getId());
        dto.setValor(precio.getValor());
        dto.setNombreDestinoOrigen(precio.getDestinoOrigen().getNombre());
        dto.setNombreDestinoLlegada(precio.getDestinoLlegada().getNombre());
        return dto;
    }
}