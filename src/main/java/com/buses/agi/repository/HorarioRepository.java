package com.buses.agi.repository;

import com.buses.agi.model.Destino;
import com.buses.agi.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    // Método para encontrar horarios por destino de origen y destino de llegada
    List<Horario> findByDestinoOrigenAndDestinoLlegada(Destino destinoOrigen, Destino destinoLlegada);

    // Método para encontrar un horario específico por origen, llegada y hora
    Optional<Horario> findByDestinoOrigenAndDestinoLlegadaAndHoraSalida(Destino destinoOrigen, Destino destinoLlegada, LocalTime horaSalida);
}