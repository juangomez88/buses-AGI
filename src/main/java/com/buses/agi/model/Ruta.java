package com.buses.agi.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rutas")
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origen_id", nullable = false)
    private Destino origen;

    @ManyToOne
    @JoinColumn(name = "destino_id", nullable = false)
    private Destino destino;
}
