package com.buses.agi.service.impl;

import com.buses.agi.DTO.PrecioDTO;
import com.buses.agi.model.Destino;
import com.buses.agi.model.Precio;
import com.buses.agi.repository.DestinoRepository;
import com.buses.agi.repository.PrecioRepository;
import com.buses.agi.service.PrecioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PrecioServiceImpl implements PrecioService {

    private final PrecioRepository precioRepository;
    private final DestinoRepository destinoRepository; // Necesitamos esto para buscar los destinos

    @Autowired
    public PrecioServiceImpl(PrecioRepository precioRepository, DestinoRepository destinoRepository) {
        this.precioRepository = precioRepository;
        this.destinoRepository = destinoRepository;
    }

    @Override
    public Optional<PrecioDTO> findPrecioByOrigenAndLlegada(Long idOrigen, Long idLlegada) {
        Optional<Destino> origen = destinoRepository.findById(idOrigen);
        Optional<Destino> llegada = destinoRepository.findById(idLlegada);

        if (origen.isPresent() && llegada.isPresent()) {
            return precioRepository.findByDestinoOrigenAndDestinoLlegada(origen.get(), llegada.get())
                    .map(this::convertToDto);
        }
        return Optional.empty(); // Retorna vacío si los destinos no se encuentran
    }

    // Método auxiliar para convertir una entidad Precio a PrecioDTO
    private PrecioDTO convertToDto(Precio precio) {
        return new PrecioDTO(
                precio.getId(),
                precio.getDestinoOrigen().getId(),
                precio.getDestinoOrigen().getNombre(),
                precio.getDestinoLlegada().getId(),
                precio.getDestinoLlegada().getNombre(),
                precio.getValor()
        );
    }
}