package com.buses.agi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "precios")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

}