package com.buses.agi.service.impl;

import com.buses.agi.DTO.ReservaRequestDTO;
import com.buses.agi.DTO.ReservaResponseDTO;
import com.buses.agi.model.Destino;
import com.buses.agi.model.Horario;
import com.buses.agi.model.Precio;
import com.buses.agi.model.Reserva;
import com.buses.agi.repository.DestinoRepository;
import com.buses.agi.repository.HorarioRepository;
import com.buses.agi.repository.PrecioRepository;
import com.buses.agi.repository.ReservaRepository;
import com.buses.agi.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final DestinoRepository destinoRepository;
    private final PrecioRepository precioRepository;
    private final HorarioRepository horarioRepository;

    @Autowired
    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              DestinoRepository destinoRepository,
                              PrecioRepository precioRepository,
                              HorarioRepository horarioRepository) {
        this.reservaRepository = reservaRepository;
        this.destinoRepository = destinoRepository;
        this.precioRepository = precioRepository;
        this.horarioRepository = horarioRepository;
    }

    @Override
    @Transactional // Asegura que toda la operación se realice de forma atómica
    public Optional<ReservaResponseDTO> crearReserva(ReservaRequestDTO reservaRequestDTO) {
        // 1. Validar y obtener Destino Origen
        Optional<Destino> origenOpt = destinoRepository.findById(reservaRequestDTO.getIdDestinoOrigen());
        if (origenOpt.isEmpty()) {
            System.err.println("Destino de origen no encontrado con ID: " + reservaRequestDTO.getIdDestinoOrigen());
            return Optional.empty();
        }
        Destino destinoOrigen = origenOpt.get();

        // 2. Validar y obtener Destino Llegada
        Optional<Destino> llegadaOpt = destinoRepository.findById(reservaRequestDTO.getIdDestinoLlegada());
        if (llegadaOpt.isEmpty()) {
            System.err.println("Destino de llegada no encontrado con ID: " + reservaRequestDTO.getIdDestinoLlegada());
            return Optional.empty();
        }
        Destino destinoLlegada = llegadaOpt.get();

        // 3. Obtener el precio para la ruta
        Optional<Precio> precioOpt = precioRepository.findByDestinoOrigenAndDestinoLlegada(destinoOrigen, destinoLlegada);
        if (precioOpt.isEmpty()) {
            System.err.println("Precio no encontrado para la ruta: " + destinoOrigen.getNombre() + " a " + destinoLlegada.getNombre());
            return Optional.empty();
        }
        BigDecimal valorTotal = precioOpt.get().getValor();

        // 4. Validar que la hora seleccionada sea un horario existente para esa ruta
        Optional<Horario> horarioOpt = horarioRepository.findByDestinoOrigenAndDestinoLlegadaAndHoraSalida(
                destinoOrigen, destinoLlegada, reservaRequestDTO.getHoraViaje());
        if (horarioOpt.isEmpty()) {
            System.err.println("Horario no válido para la ruta y hora especificadas: " + reservaRequestDTO.getHoraViaje());
            return Optional.empty();
        }

        // 5. Crear la entidad Reserva
        Reserva reserva = new Reserva();
        reserva.setDestinoOrigen(destinoOrigen);
        reserva.setDestinoLlegada(destinoLlegada);
        reserva.setFechaViaje(reservaRequestDTO.getFechaViaje());
        reserva.setHoraViaje(reservaRequestDTO.getHoraViaje());
        reserva.setValorTotal(valorTotal);
        reserva.setEstado("PENDIENTE"); // Establecemos un estado inicial

        // 6. Guardar la reserva en la base de datos
        Reserva savedReserva = reservaRepository.save(reserva);

        // 7. Convertir la entidad guardada a DTO de respuesta
        return Optional.of(convertToDto(savedReserva));
    }

    @Override
    public Optional<ReservaResponseDTO> getReservaById(Long id) {
        return reservaRepository.findById(id)
                .map(this::convertToDto);
    }

    // Método auxiliar para convertir una entidad Reserva a ReservaResponseDTO
    private ReservaResponseDTO convertToDto(Reserva reserva) {
        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getDestinoOrigen().getNombre(),
                reserva.getDestinoLlegada().getNombre(),
                reserva.getFechaViaje(),
                reserva.getHoraViaje(),
                reserva.getValorTotal(),
                reserva.getEstado()
        );
    }
}