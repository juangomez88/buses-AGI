package com.buses.agi.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "horarios")
public class Horario {
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
    private LocalTime horaSalida;
    
    @Column(nullable = false)
    private Boolean activo = true;
}