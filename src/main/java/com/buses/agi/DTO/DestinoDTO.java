package com.buses.agi.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DestinoDTO {
    private Long id;
    private String nombre;
    private Boolean activo; // necesario porque tu servicio lo asigna
}
