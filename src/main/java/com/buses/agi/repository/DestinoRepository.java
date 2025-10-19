package com.buses.agi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buses.agi.model.Destino;

import java.util.Optional;

@Repository
public interface DestinoRepository extends JpaRepository<Destino, Long> {
    Optional<Destino> findByNombreAndActivoTrue(String nombre);
    Optional<Destino> findByIdAndActivoTrue(Long id);
}