package com.buses.agi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "horarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hora_salida", nullable = false)
    private LocalTime horaSalida;

    @ManyToOne
    @JoinColumn(name = "id_destino_origen", nullable = false)
    private Destino destinoOrigen;

    @ManyToOne
    @JoinColumn(name = "id_destino_llegada", nullable = false)
    private Destino destinoLlegada;

}