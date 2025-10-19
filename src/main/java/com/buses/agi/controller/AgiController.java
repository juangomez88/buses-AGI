package com.buses.agi.controller;

import com.buses.agi.DTO.DestinoDTO;
import com.buses.agi.DTO.HorarioDTO;
import com.buses.agi.DTO.PrecioDTO;
import com.buses.agi.DTO.ReservaRequestDTO;
import com.buses.agi.DTO.ReservaResponseDTO;
import com.buses.agi.service.DestinoService;
import com.buses.agi.service.HorarioService;
import com.buses.agi.service.PrecioService;
import com.buses.agi.service.ReservaService;
import lombok.extern.slf4j.Slf4j;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class AgiController extends BaseAgiScript {

    @Autowired
    private DestinoService destinoService;

    @Autowired
    private HorarioService horarioService;

    @Autowired
    private PrecioService precioService;

    @Autowired
    private ReservaService reservaService;

    private final Map<String, ReservaRequestDTO> currentReservations = new ConcurrentHashMap<>();
    private final Map<String, String> currentOriginNames = new ConcurrentHashMap<>();
    private final Map<String, String> currentDestinationNames = new ConcurrentHashMap<>();

    @Override
    public void service(AgiRequest request, AgiChannel channel) throws AgiException {
        String callerId = request.getCallerId();
        log.info("Llamada AGI entrante desde callerId: {}", callerId);

        currentReservations.putIfAbsent(callerId, new ReservaRequestDTO());

        try {
            bienvenida(channel);
            mainMenu(channel, callerId);
        } catch (Exception e) {
            log.error("Error durante la interacción de AGI para callerId {}: {}", callerId, e.getMessage(), e);
            channel.streamFile("vm-error");
            channel.hangup();
        } finally {
            currentReservations.remove(callerId);
            currentOriginNames.remove(callerId);
            currentDestinationNames.remove(callerId);
            log.info("Llamada AGI finalizada para callerId: {}", callerId);
        }
    }

    private void bienvenida(AgiChannel channel) throws AgiException {
        channel.exec("AGI", "googletts.agi,\"Bienvenido al sistema de reserva de buses.\",es");
    }

    private void mainMenu(AgiChannel channel, String callerId) throws AgiException {
        channel.exec("AGI", "googletts.agi,\"Presione 1 para realizar una reserva, 2 para salir.\",es");

        String digit = String.valueOf(channel.waitForDigit(10000));
        if (digit == null) {
            channel.exec("AGI", "googletts.agi,\"No se detectó ninguna opción. Adiós.\",es");
            channel.hangup();
            return;
        }

        if ("1".equals(digit)) {
            seleccionarOrigen(channel, callerId);
        } else if ("2".equals(digit)) {
            channel.exec("AGI", "googletts.agi,\"Gracias por usar nuestro servicio. Adiós.\",es");
            channel.hangup();
        } else {
            channel.exec("AGI", "googletts.agi,\"Opción no válida. Adiós.\",es");
            channel.hangup();
        }
    }

    private void seleccionarOrigen(AgiChannel channel, String callerId) throws AgiException {
        channel.exec("AGI", "googletts.agi,\"Seleccione ciudad de origen: 1 Medellín, 2 Bogotá, 3 Cali, 4 Cartagena.\",es");

        String digit = String.valueOf(channel.waitForDigit(10000));
        if (digit == null) {
            channel.exec("AGI", "googletts.agi,\"No se detectó entrada. Volviendo al menú.\",es");
            mainMenu(channel, callerId);
            return;
        }

        String ciudad = obtenerCiudadPorOpcion(digit);
        if (ciudad == null) {
            channel.exec("AGI", "googletts.agi,\"Opción no válida. Volviendo al menú.\",es");
            mainMenu(channel, callerId);
            return;
        }

        Optional<DestinoDTO> destinoOpt = destinoService.findDestinoByNombre(ciudad);
        if (destinoOpt.isEmpty()) {
            channel.exec("AGI", "googletts.agi,\"Ciudad no disponible. Volviendo al menú.\",es");
            mainMenu(channel, callerId);
            return;
        }

        DestinoDTO destino = destinoOpt.get();
        ReservaRequestDTO reserva = currentReservations.get(callerId);
        reserva.setIdDestinoOrigen(destino.getId());
        currentOriginNames.put(callerId, destino.getNombre());

        log.info("Origen seleccionado: {} (ID {})", destino.getNombre(), destino.getId());
        seleccionarDestino(channel, callerId);
    }

    private void seleccionarDestino(AgiChannel channel, String callerId) throws AgiException {
        Long idOrigen = currentReservations.get(callerId).getIdDestinoOrigen();
        if (idOrigen == null) {
            channel.exec("AGI", "googletts.agi,\"Error: Origen no seleccionado. Volviendo al menú.\",es");
            mainMenu(channel, callerId);
            return;
        }

        channel.exec("AGI", "googletts.agi,\"Seleccione ciudad de destino: 1 Medellín, 2 Bogotá, 3 Cali, 4 Cartagena.\",es");

        String digit = String.valueOf(channel.waitForDigit(10000));
        if (digit == null) {
            channel.exec("AGI", "googletts.agi,\"No se detectó entrada. Volviendo al menú.\",es");
            mainMenu(channel, callerId);
            return;
        }

        String ciudad = obtenerCiudadPorOpcion(digit);
        if (ciudad == null) {
            channel.exec("AGI", "googletts.agi,\"Opción no válida. Volviendo al menú.\",es");
            mainMenu(channel, callerId);
            return;
        }

        Optional<DestinoDTO> destinoOpt = destinoService.findDestinoByNombre(ciudad);
        if (destinoOpt.isEmpty()) {
            channel.exec("AGI", "googletts.agi,\"Ciudad no disponible. Volviendo al menú.\",es");
            mainMenu(channel, callerId);
            return;
        }

        DestinoDTO destino = destinoOpt.get();

        if (destino.getId().equals(idOrigen)) {
            channel.exec("AGI", "googletts.agi,\"El destino no puede ser igual al origen. Volviendo al menú.\",es");
            mainMenu(channel, callerId);
            return;
        }

        ReservaRequestDTO reserva = currentReservations.get(callerId);
        reserva.setIdDestinoLlegada(destino.getId());
        currentDestinationNames.put(callerId, destino.getNombre());

        log.info("Destino seleccionado: {} (ID {})", destino.getNombre(), destino.getId());
        seleccionarFecha(channel, callerId);
    }

    private void seleccionarFecha(AgiChannel channel, String callerId) throws AgiException {
        channel.exec("AGI", "googletts.agi,\"Ingrese la fecha en formato AAAAMMDD, por ejemplo 20241225 para Navidad.\",es");

        String fechaInput = channel.getData("beep", 15000, 8);
        if (fechaInput == null || fechaInput.length() != 8) {
            channel.exec("AGI", "googletts.agi,\"Formato incorrecto. Volviendo al menú.\",es");
            mainMenu(channel, callerId);
            return;
        }

        try {
            int year = Integer.parseInt(fechaInput.substring(0, 4));
            int month = Integer.parseInt(fechaInput.substring(4, 6));
            int day = Integer.parseInt(fechaInput.substring(6, 8));

            LocalDate fechaViaje = LocalDate.of(year, month, day);

            if (fechaViaje.isBefore(LocalDate.now())) {
                channel.exec("AGI", "googletts.agi,\"No se permiten fechas pasadas. Volviendo al menú.\",es");
                mainMenu(channel, callerId);
                return;
            }

            currentReservations.get(callerId).setFechaViaje(fechaViaje);
            seleccionarHora(channel, callerId);
        } catch (Exception e) {
            log.error("Error al procesar fecha: {}", e.getMessage());
            channel.exec("AGI", "googletts.agi,\"Error en fecha. Volviendo al menú.\",es");
            mainMenu(channel, callerId);
        }
    }

    private void seleccionarHora(AgiChannel channel, String callerId) throws AgiException {
        ReservaRequestDTO reserva = currentReservations.get(callerId);
        List<HorarioDTO> horarios = horarioService.findHorariosByOrigenAndLlegada(
            reserva.getIdDestinoOrigen(), reserva.getIdDestinoLlegada());

        if (horarios.isEmpty()) {
            channel.exec("AGI", "googletts.agi,\"No hay horarios disponibles. Volviendo al menú.\",es");
            mainMenu(channel, callerId);
            return;
        }

        channel.exec("AGI", "googletts.agi,\"Seleccione horario:\",es");
        for (int i = 0; i < horarios.size(); i++) {
            String horaStr = horarios.get(i).getHoraSalida().format(DateTimeFormatter.ofPattern("HH:mm"));
            channel.exec("AGI", String.format("googletts.agi,\"%d para las %s.\",es", (i + 1), horaStr));
        }

        String digit = String.valueOf(channel.waitForDigit(10000));
        if (digit == null) {
            channel.exec("AGI", "googletts.agi,\"No se detectó opción. Volviendo al menú.\",es");
            mainMenu(channel, callerId);
            return;
        }

        try {
            int choice = Integer.parseInt(digit);
            if (choice >= 1 && choice <= horarios.size()) {
                HorarioDTO horario = horarios.get(choice - 1);
                reserva.setHoraViaje(horario.getHoraSalida());
                confirmarReserva(channel, callerId);
            } else {
                channel.exec("AGI", "googletts.agi,\"Opción no válida. Volviendo al menú.\",es");
                mainMenu(channel, callerId);
            }
        } catch (NumberFormatException e) {
            channel.exec("AGI", "googletts.agi,\"Opción inválida. Volviendo al menú.\",es");
            mainMenu(channel, callerId);
        }
    }

    private void confirmarReserva(AgiChannel channel, String callerId) throws AgiException {
        ReservaRequestDTO reservaRequest = currentReservations.get(callerId);
        
        Optional<PrecioDTO> precioOpt = precioService.findPrecioByOrigenAndLlegada(
            reservaRequest.getIdDestinoOrigen(), reservaRequest.getIdDestinoLlegada());
        
        if (precioOpt.isEmpty()) {
            channel.exec("AGI", "googletts.agi,\"Error: No se pudo obtener el precio. Volviendo al menú.\",es");
            mainMenu(channel, callerId);
            return;
        }

        String origen = currentOriginNames.get(callerId);
        String destino = currentDestinationNames.get(callerId);
        String fecha = reservaRequest.getFechaViaje().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String hora = reservaRequest.getHoraViaje().format(DateTimeFormatter.ofPattern("HH:mm"));
        String precio = precioOpt.get().getValor().toString();

        channel.exec("AGI", "googletts.agi,\"Resumen: De " + origen + " a " + destino + ".\",es");
        channel.exec("AGI", "googletts.agi,\"Fecha: " + fecha + " Hora: " + hora + ".\",es");
        channel.exec("AGI", "googletts.agi,\"Precio: " + precio + " pesos.\",es");
        channel.exec("AGI", "googletts.agi,\"Presione 1 para confirmar, cualquier otra tecla para cancelar.\",es");

        String digit = String.valueOf(channel.waitForDigit(10000));
        if ("1".equals(digit)) {
            Optional<ReservaResponseDTO> reservaConfirmada = reservaService.crearReserva(reservaRequest);
            if (reservaConfirmada.isPresent()) {
                channel.exec("AGI", "googletts.agi,\"Reserva confirmada. Número: " + reservaConfirmada.get().getId() + ". Gracias.\",es");
            } else {
                channel.exec("AGI", "googletts.agi,\"Error al confirmar reserva. Intente más tarde.\",es");
            }
        } else {
            channel.exec("AGI", "googletts.agi,\"Reserva cancelada. Adiós.\",es");
        }
        channel.hangup();
    }

    private String obtenerCiudadPorOpcion(String opcion) {
        return switch (opcion) {
            case "1" -> "Medellin";
            case "2" -> "Bogotá";
            case "3" -> "Cali";
            case "4" -> "Cartagena";
            default -> null;
        };
    }
}