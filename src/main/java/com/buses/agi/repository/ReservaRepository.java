package com.buses.agi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buses.agi.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}