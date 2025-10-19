package com.buses.agi.service.impl;

import com.buses.agi.DTO.ReservaRequestDTO;
import com.buses.agi.DTO.ReservaResponseDTO;
import com.buses.agi.model.Destino;
import com.buses.agi.model.Reserva;
import com.buses.agi.repository.DestinoRepository;
import com.buses.agi.repository.PrecioRepository;
import com.buses.agi.repository.ReservaRepository;
import com.buses.agi.service.ReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final DestinoRepository destinoRepository;
    private final PrecioRepository precioRepository;

    @Override
    @Transactional
    public Optional<ReservaResponseDTO> crearReserva(ReservaRequestDTO reservaRequest) {
        try {
            // Validar que los destinos existen
            Optional<Destino> origenOpt = destinoRepository.findByIdAndActivoTrue(reservaRequest.getIdDestinoOrigen());
            Optional<Destino> llegadaOpt = destinoRepository.findByIdAndActivoTrue(reservaRequest.getIdDestinoLlegada());
            
            if (origenOpt.isEmpty() || llegadaOpt.isEmpty()) {
                log.error("Destinos no encontrados: origen={}, llegada={}", 
                         reservaRequest.getIdDestinoOrigen(), reservaRequest.getIdDestinoLlegada());
                return Optional.empty();
            }

            // Obtener el precio
            var precioOpt = precioRepository.findByOrigenAndLlegada(
                reservaRequest.getIdDestinoOrigen(), reservaRequest.getIdDestinoLlegada());
            
            if (precioOpt.isEmpty()) {
                log.error("Precio no encontrado para la ruta: origen={}, llegada={}", 
                         reservaRequest.getIdDestinoOrigen(), reservaRequest.getIdDestinoLlegada());
                return Optional.empty();
            }

            // Crear la reserva
            Reserva reserva = new Reserva();
            reserva.setDestinoOrigen(origenOpt.get());
            reserva.setDestinoLlegada(llegadaOpt.get());
            reserva.setFechaViaje(reservaRequest.getFechaViaje());
            reserva.setHoraViaje(reservaRequest.getHoraViaje());
            reserva.setValorTotal(precioOpt.get().getValor());

            Reserva reservaGuardada = reservaRepository.save(reserva);
            log.info("Reserva creada exitosamente: ID {}", reservaGuardada.getId());

            return Optional.of(convertToDTO(reservaGuardada));

        } catch (Exception e) {
            log.error("Error al crear reserva", e);
            return Optional.empty();
        }
    }

    private ReservaResponseDTO convertToDTO(Reserva reserva) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(reserva.getId());
        dto.setIdDestinoOrigen(reserva.getDestinoOrigen().getId());
        dto.setIdDestinoLlegada(reserva.getDestinoLlegada().getId());
        dto.setNombreDestinoOrigen(reserva.getDestinoOrigen().getNombre());
        dto.setNombreDestinoLlegada(reserva.getDestinoLlegada().getNombre());
        dto.setFechaViaje(reserva.getFechaViaje());
        dto.setHoraViaje(reserva.getHoraViaje());
        dto.setValorTotal(reserva.getValorTotal());
        dto.setEstado(reserva.getEstado());
        return dto;
    }
}