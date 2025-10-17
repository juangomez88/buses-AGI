package com.buses.agi.service.impl;

import com.buses.agi.DTO.HorarioDTO;
import com.buses.agi.model.Destino;
import com.buses.agi.model.Horario;
import com.buses.agi.repository.DestinoRepository;
import com.buses.agi.repository.HorarioRepository;
import com.buses.agi.service.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HorarioServiceImpl implements HorarioService {

    private final HorarioRepository horarioRepository;
    private final DestinoRepository destinoRepository;

    @Autowired
    public HorarioServiceImpl(HorarioRepository horarioRepository, DestinoRepository destinoRepository) {
        this.horarioRepository = horarioRepository;
        this.destinoRepository = destinoRepository;
    }

    @Override
    public List<HorarioDTO> findHorariosByOrigenAndLlegada(Long idOrigen, Long idLlegada) {
        Optional<Destino> origen = destinoRepository.findById(idOrigen);
        Optional<Destino> llegada = destinoRepository.findById(idLlegada);

        if (origen.isPresent() && llegada.isPresent()) {
            return horarioRepository.findByDestinoOrigenAndDestinoLlegada(origen.get(), llegada.get()).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public Optional<HorarioDTO> findHorarioById(Long id) {
        return horarioRepository.findById(id)
                .map(this::convertToDto);
    }

    // Método auxiliar para convertir una entidad Horario a HorarioDTO
    private HorarioDTO convertToDto(Horario horario) {
        return new HorarioDTO(
                horario.getId(),
                horario.getHoraSalida(),
                horario.getDestinoOrigen().getId(),
                horario.getDestinoOrigen().getNombre(),
                horario.getDestinoLlegada().getId(),
                horario.getDestinoLlegada().getNombre()
        );
    }
}