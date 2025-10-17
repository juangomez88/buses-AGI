package com.buses.agi.repository;

import com.buses.agi.model.Destino;
import com.buses.agi.model.Precio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrecioRepository extends JpaRepository<Precio, Long> {

    // Método para encontrar un precio por destino de origen y destino de llegada
    Optional<Precio> findByDestinoOrigenAndDestinoLlegada(Destino destinoOrigen, Destino destinoLlegada);
}