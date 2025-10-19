package com.buses.agi.model;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "destinos")
public class Destino {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nombre;
    
    @Column(nullable = false)
    private Boolean activo = true;
}