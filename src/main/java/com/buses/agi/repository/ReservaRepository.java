package com.buses.agi.repository;

import com.buses.agi.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Buscar reservas por una fecha específica
    List<Reserva> findByFechaViaje(LocalDate fechaViaje);

    // Si quieres buscar entre dos fechas (rango)
    List<Reserva> findByFechaViajeBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Si quieres buscar por fecha y estado (opcional)
    List<Reserva> findByFechaViajeAndEstado(LocalDate fechaViaje, String estado);
}