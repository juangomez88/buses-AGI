package com.buses.agi.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_destino_origen", nullable = false)
    private Destino destinoOrigen;
    
    @ManyToOne
    @JoinColumn(name = "id_destino_llegada", nullable = false)
    private Destino destinoLlegada;
    
    @Column(nullable = false)
    private LocalDate fechaViaje;
    
    @Column(nullable = false)
    private LocalTime horaViaje;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;
    
    @Column(nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();
    
    @Column(nullable = false)
    private String estado = "CONFIRMADA";
}