package com.buses.agi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.buses.agi.model.Precio;

import java.util.Optional;

@Repository
public interface PrecioRepository extends JpaRepository<Precio, Long> {
    @Query("SELECT p FROM Precio p WHERE p.destinoOrigen.id = :origenId AND p.destinoLlegada.id = :llegadaId AND p.activo = true")
    Optional<Precio> findByOrigenAndLlegada(@Param("origenId") Long origenId, @Param("llegadaId") Long llegadaId);
}