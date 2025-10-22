package com.buses.agi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.buses.agi.model.Destino;

import java.util.Optional;

@Repository
public interface DestinoRepository extends JpaRepository<Destino, Long> {
    Optional<Destino> findByNombreAndActivoTrue(String nombre);
    Optional<Destino> findByIdAndActivoTrue(Long id);

    @Query("SELECT d FROM Destino d WHERE LOWER(FUNCTION('REPLACE', d.nombre, 'í', 'i')) = LOWER(FUNCTION('REPLACE', :nombre, 'í', 'i'))")
    Optional<Destino> findDestinoByNombreIgnoreAccents(@Param("nombre") String nombre);

}