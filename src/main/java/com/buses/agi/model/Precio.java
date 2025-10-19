package com.buses.agi.model;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "precios")
public class Precio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_destino_origen", nullable = false)
    private Destino destinoOrigen;
    
    @ManyToOne
    @JoinColumn(name = "id_destino_llegada", nullable = false)
    private Destino destinoLlegada;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @Column(nullable = false)
    private Boolean activo = true;
}