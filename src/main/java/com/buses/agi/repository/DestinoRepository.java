package com.buses.agi.repository;

import com.buses.agi.model.Destino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DestinoRepository extends JpaRepository<Destino, Long> {

    Optional<Destino> findByNombre(String nombre);
}