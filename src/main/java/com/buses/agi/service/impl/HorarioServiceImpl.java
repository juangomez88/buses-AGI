package com.buses.agi.service.impl;

import com.buses.agi.DTO.HorarioDTO;
import com.buses.agi.model.Horario;
import com.buses.agi.repository.HorarioRepository;
import com.buses.agi.service.HorarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HorarioServiceImpl implements HorarioService {

    private final HorarioRepository horarioRepository;

    @Override
    public List<HorarioDTO> findHorariosByOrigenAndLlegada(Long origenId, Long llegadaId) {
        try {
            List<Horario> horarios = horarioRepository.findByOrigenAndLlegada(origenId, llegadaId);
            return horarios.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al buscar horarios para origen {} y llegada {}", origenId, llegadaId, e);
            return List.of();
        }
    }

    private HorarioDTO convertToDTO(Horario horario) {
        HorarioDTO dto = new HorarioDTO();
        dto.setId(horario.getId());
        dto.setIdDestinoOrigen(horario.getDestinoOrigen().getId());
        dto.setIdDestinoLlegada(horario.getDestinoLlegada().getId());
        dto.setHoraSalida(horario.getHoraSalida());
        dto.setNombreDestinoOrigen(horario.getDestinoOrigen().getNombre());
        dto.setNombreDestinoLlegada(horario.getDestinoLlegada().getNombre());
        return dto;
    }
}