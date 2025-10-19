package com.buses.agi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.buses.agi.model.Horario;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {
    @Query("SELECT h FROM Horario h WHERE h.destinoOrigen.id = :origenId AND h.destinoLlegada.id = :llegadaId AND h.activo = true ORDER BY h.horaSalida")
    List<Horario> findByOrigenAndLlegada(@Param("origenId") Long origenId, @Param("llegadaId") Long llegadaId);
}