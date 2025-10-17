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
import java.time.LocalTime;
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

    public AgiController() {
        log.info("AgiController constructor vacío llamado (usado por Asterisk-Java)");
    }


    private final Map<String, ReservaRequestDTO> currentReservations = new ConcurrentHashMap<>();
    private final Map<String, String> currentOriginNames = new ConcurrentHashMap<>();
    private final Map<String, String> currentDestinationNames = new ConcurrentHashMap<>();


    @Autowired
    public AgiController(DestinoService destinoService,
                         HorarioService horarioService,
                         PrecioService precioService,
                         ReservaService reservaService) {
        this.destinoService = destinoService;
        this.horarioService = horarioService;
        this.precioService = precioService;
        log.info("AgiController initialized with services.");
        this.reservaService = reservaService;
    }

    @Override
    public void service(AgiRequest request, AgiChannel channel) throws AgiException {
        // Obtenemos el ID único del canal para manejar el estado de cada llamada
        String callerId = request.getCallerId();
        log.info("Incoming AGI call from callerId: {}", callerId);

        // Inicializamos o recuperamos la reserva en curso para este canal
        currentReservations.putIfAbsent(callerId, new ReservaRequestDTO());

        try {
            bienvenida(channel);
            mainMenu(channel, callerId);
        } catch (Exception e) {
            log.error("Error during AGI interaction for callerId {}: {}", callerId, e.getMessage(), e);
            channel.streamFile("vm-error");
            channel.hangup();
        } finally {
            currentReservations.remove(callerId);
            currentOriginNames.remove(callerId);
            currentDestinationNames.remove(callerId);
            log.info("AGI call finished for callerId: {}", callerId);
        }
    }

    private void bienvenida(AgiChannel channel) throws AgiException {
        channel.exec("AGI", "googletts.agi,\"Bienvenido al Agi de transporte de buses.\",es");
        log.debug("Bienvenida reproducida.");
    }

    private void mainMenu(AgiChannel channel, String callerId) throws AgiException {
        int choice = -1;
        while (choice != 2) {
            channel.exec("AGI", "googletts.agi,\"Seleccione la opción que necesita: \",es");
            channel.exec("AGI", "googletts.agi,\"1 para seleccionar destino.\",es");
            channel.exec("AGI", "googletts.agi,\"2 para colgar.\",es");

            String digit = String.valueOf(channel.waitForDigit(-1)); // Espera por un dígito indefinidamente
            if (digit != null) {
                choice = Integer.parseInt(digit);
                log.debug("Main menu choice for {}: {}", callerId, choice);
                switch (choice) {
                    case 1:
                        seleccionarOrigen(channel, callerId);
                        break;
                    case 2:
                        channel.exec("AGI", "googletts.agi,\"Gracias por usar nuestro servicio. Adiós.\",es");
                        channel.hangup();
                        break;
                    default:
                        channel.exec("AGI", "googletts.agi,\"Opción no válida, por favor intente de nuevo.\",es");
                        break;
                }
            } else {
                channel.exec("AGI", "googletts.agi,\"No se detectó ninguna opción. Intente de nuevo.\",es");
            }
        }
    }


    private void seleccionarOrigen(AgiChannel channel, String callerId) throws AgiException {
        int choice = -1;
        while (true) {
            channel.exec("AGI", "googletts.agi,\"Seleccione la ciudad de origen del viaje:\",es");
            channel.exec("AGI", "googletts.agi,\"1 para Medellin, 2 para Bogotá, 3 para Cali, 4 para Cartagena, 5 para colgar.\",es");

            String digitStr = String.valueOf(channel.waitForDigit(-1));
            if (digitStr == null) {
                channel.exec("AGI", "googletts.agi,\"No se detectó entrada. Intente de nuevo.\",es");
                continue;
            }

            try {
                choice = Integer.parseInt(digitStr);
            } catch (NumberFormatException e) {
                channel.exec("AGI", "googletts.agi,\"Entrada inválida. Intente de nuevo.\",es");
                continue;
            }

            if (choice == 5) {
                channel.exec("AGI", "googletts.agi,\"Gracias por usar nuestro servicio. Adiós.\",es");
                channel.hangup();
                return;
            }

            String ciudad = switch (choice) {
                case 1 -> "Medellin";
                case 2 -> "Bogotá";
                case 3 -> "Cali";
                case 4 -> "Cartagena";
                default -> null;
            };

            if (ciudad == null) {
                channel.exec("AGI", "googletts.agi,\"Opción no válida. Intente de nuevo.\",es");
                continue;
            }

            // Buscar el destino en la base de datos (por nombre)
            Optional<DestinoDTO> destinoOpt = destinoService.findDestinoByNombre(ciudad);
            if (destinoOpt.isEmpty()) {
                channel.exec("AGI", "googletts.agi,\"Lo sentimos, la ciudad " + ciudad + " no está disponible en este momento.\",es");
                channel.exec("AGI", "googletts.agi,\"Contacte al administrador o intente otra opción.\",es");
                // no salimos; permitimos otro intento
                continue;
            }

            DestinoDTO destino = destinoOpt.get();
            // Guardar id y nombre en la reserva en curso
            ReservaRequestDTO reserva = currentReservations.get(callerId);
            reserva.setIdDestinoOrigen(destino.getId());
            currentReservations.put(callerId, reserva);
            currentOriginNames.put(callerId, destino.getNombre());

            log.info("Origen seleccionado para {}: {} (ID {})", callerId, destino.getNombre(), destino.getId());

            // Ir a seleccionar destino
            seleccionarDestino(channel, callerId);
            return;
        }
    }

    private void seleccionarDestino(AgiChannel channel, String callerId) throws AgiException {
        Long idOrigen = currentReservations.get(callerId).getIdDestinoOrigen();
        if (idOrigen == null) {
            channel.exec("AGI", "googletts.agi,\"Error: Origen no seleccionado. Volviendo al menú principal.\",es");
            mainMenu(channel, callerId);
            return;
        }

        int choice = -1;
        while (true) {
            channel.exec("AGI", "googletts.agi,\"Seleccione la ciudad de destino (no puede ser la misma que el origen):\",es");
            channel.exec("AGI", "googletts.agi,\"1 para Medellin, 2 para Bogotá, 3 para Cali, 4 para Cartagena, 5 para colgar.\",es");

            String digitStr = String.valueOf(channel.waitForDigit(-1));
            if (digitStr == null) {
                channel.exec("AGI", "googletts.agi,\"No se detectó entrada. Intente de nuevo.\",es");
                continue;
            }

            try {
                choice = Integer.parseInt(digitStr);
            } catch (NumberFormatException e) {
                channel.exec("AGI", "googletts.agi,\"Entrada inválida. Intente de nuevo.\",es");
                continue;
            }

            if (choice == 5) {
                channel.exec("AGI", "googletts.agi,\"Gracias por usar nuestro servicio. Adiós.\",es");
                channel.hangup();
                return;
            }

            String ciudad = switch (choice) {
                case 1 -> "Medellin";
                case 2 -> "Bogotá";
                case 3 -> "Cali";
                case 4 -> "Cartagena";
                default -> null;
            };

            if (ciudad == null) {
                channel.exec("AGI", "googletts.agi,\"Opción no válida. Intente de nuevo.\",es");
                continue;
            }

            Optional<DestinoDTO> destinoOpt = destinoService.findDestinoByNombre(ciudad);
            if (destinoOpt.isEmpty()) {
                channel.exec("AGI", "googletts.agi,\"Lo sentimos, la ciudad " + ciudad + " no está disponible en este momento.\",es");
                continue;
            }

            DestinoDTO destino = destinoOpt.get();

            // Verificar que no sea el mismo ID que el origen
            if (destino.getId().equals(idOrigen)) {
                channel.exec("AGI", "googletts.agi,\"El destino no puede ser el mismo que el origen. Por favor, seleccione otra opción.\",es");
                continue;
            }

            // Guardar id y nombre del destino
            ReservaRequestDTO reserva = currentReservations.get(callerId);
            reserva.setIdDestinoLlegada(destino.getId());
            currentReservations.put(callerId, reserva);
            currentDestinationNames.put(callerId, destino.getNombre());

            log.info("Destino seleccionado para {}: {} (ID {})", callerId, destino.getNombre(), destino.getId());

            channel.exec("AGI", "googletts.agi,\"Usted viajará de " + currentOriginNames.get(callerId) + " a " + destino.getNombre() + ".\",es");

            seleccionarFecha(channel, callerId);
            return;
        }
    }

    private void seleccionarFecha(AgiChannel channel, String callerId) throws AgiException {
        channel.exec("AGI", "googletts.agi,\"Por favor, ingrese la fecha de su viaje en formato AAAA MM DD, seguido de la tecla numeral.\",es");
        channel.exec("AGI", "googletts.agi,\"Por ejemplo, para el 15 de octubre de 2024, marque 2024 10 15 numeral.\",es");

        String fechaInput = channel.getData("beep", 10000, 10).trim(); // Espera hasta 10 segundos, 10 dígitos
        log.debug("Fecha input for {}: {}", callerId, fechaInput);

        if (fechaInput == null || fechaInput.isEmpty()) {
            channel.exec("AGI", "googletts.agi,\"No se detectó ninguna entrada. Volviendo a la selección de destino.\",es");
            seleccionarDestino(channel, callerId);
            return;
        }

        try {
            // Asumiendo que el usuario ingresa AAAA MM DD (ej. 20241015)
            if (fechaInput.length() != 8) {
                channel.exec("AGI", "googletts.agi,\"Formato de fecha incorrecto. Debe ser AAAA MM DD. Por favor, intente de nuevo.\",es");
                seleccionarFecha(channel, callerId); // Repetir
                return;
            }

            int year = Integer.parseInt(fechaInput.substring(0, 4));
            int month = Integer.parseInt(fechaInput.substring(4, 6));
            int day = Integer.parseInt(fechaInput.substring(6, 8));

            LocalDate fechaViaje = LocalDate.of(year, month, day);

            // Validación básica: no permitir fechas pasadas
            if (fechaViaje.isBefore(LocalDate.now())) {
                channel.exec("AGI", "googletts.agi,\"No se pueden seleccionar fechas pasadas. Por favor, intente de nuevo.\",es");
                seleccionarFecha(channel, callerId); // Repetir
                return;
            }

            currentReservations.get(callerId).setFechaViaje(fechaViaje);
            log.debug("Fecha seleccionada para {}: {}", callerId, fechaViaje);
            seleccionarHora(channel, callerId);
        } catch (Exception e) {
            log.error("Error al procesar la fecha para {}: {}", callerId, e.getMessage(), e);
            channel.exec("AGI", "googletts.agi,\"Error al procesar la fecha. Por favor, ingrese un formato válido.\",es");
            seleccionarFecha(channel, callerId); // Repetir
        }
    }

    private void seleccionarHora(AgiChannel channel, String callerId) throws AgiException {
        Long idOrigen = currentReservations.get(callerId).getIdDestinoOrigen();
        Long idLlegada = currentReservations.get(callerId).getIdDestinoLlegada();

        if (idOrigen == null || idLlegada == null) {
            channel.exec("AGI", "googletts.agi,\"Error: Origen o destino no seleccionados. Volviendo a la selección de destino.\",es");
            seleccionarDestino(channel, callerId);
            return;
        }

        List<HorarioDTO> horarios = horarioService.findHorariosByOrigenAndLlegada(idOrigen, idLlegada);
        if (horarios.isEmpty()) {
            channel.exec("AGI", "googletts.agi,\"Lo sentimos, no hay horarios disponibles para esta ruta.\",es");
            seleccionarFecha(channel, callerId); // Volver a seleccionar fecha si no hay horarios
            return;
        }

        int choice = -1;
        while (choice == -1 || choice == 5) { // 5 para repetir
            channel.exec("AGI", "googletts.agi,\"Seleccione la hora de salida de su viaje: \",es");
            for (int i = 0; i < horarios.size(); i++) {
                channel.exec("AGI", String.format("googletts.agi,\"%d para las %s.\",es", (i + 1), horarios.get(i).getHoraSalida().format(DateTimeFormatter.ofPattern("HH 'horas' mm 'minutos'"))));
            }
            channel.exec("AGI", "googletts.agi,\"5 para repetir de nuevo.\",es");
            channel.exec("AGI", "googletts.agi,\"6 para salir.\",es");

            String digit = String.valueOf(channel.waitForDigit(-1));
            if (digit != null) {
                try {
                    choice = Integer.parseInt(digit);
                    if (choice >= 1 && choice <= horarios.size()) {
                        HorarioDTO horarioSeleccionado = horarios.get(choice - 1);
                        currentReservations.get(callerId).setHoraViaje(horarioSeleccionado.getHoraSalida());
                        log.debug("Hora seleccionada para {}: {}", callerId, horarioSeleccionado.getHoraSalida());
                        confirmarReserva(channel, callerId);
                        return;
                    } else if (choice == 5) {
                        channel.exec("AGI", "googletts.agi,\"Repitiendo opciones de hora.\",es");
                    } else if (choice == 6) {
                        channel.exec("AGI", "googletts.agi,\"Saliendo del proceso de reserva. Adiós.\",es");
                        channel.hangup();
                        return;
                    } else {
                        channel.exec("AGI", "googletts.agi,\"Opción no válida, por favor intente de nuevo.\",es");
                        choice = -1;
                    }
                } catch (NumberFormatException e) {
                    channel.exec("AGI", "googletts.agi,\"Entrada no válida. Por favor, introduzca un número.\",es");
                    choice = -1;
                }
            } else {
                channel.exec("AGI", "googletts.agi,\"No se detectó ninguna opción. Intente de nuevo.\",es");
                choice = -1;
            }
        }
    }

    private void confirmarReserva(AgiChannel channel, String callerId) throws AgiException {
        ReservaRequestDTO reservaRequest = currentReservations.get(callerId);
        String origen = currentOriginNames.get(callerId);
        String destino = currentDestinationNames.get(callerId);
        LocalDate fecha = reservaRequest.getFechaViaje();
        LocalTime hora = reservaRequest.getHoraViaje();

        Optional<PrecioDTO> precioOpt = precioService.findPrecioByOrigenAndLlegada(reservaRequest.getIdDestinoOrigen(), reservaRequest.getIdDestinoLlegada());
        String valorStr = precioOpt.map(p -> p.getValor().toString()).orElse("valor no disponible");

        channel.exec("AGI", "googletts.agi,\"Su reserva es para viajar de " + origen + " a " + destino + ".\",es");
        channel.exec("AGI", "googletts.agi,\"El " + fecha.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")) + " a las " + hora.format(DateTimeFormatter.ofPattern("HH 'horas' mm 'minutos'")) + ".\",es");
        channel.exec("AGI", "googletts.agi,\"Con un valor de " + valorStr + " pesos.\",es");
        channel.exec("AGI", "googletts.agi,\"Presione 1 para confirmar, o 2 para corregir.\",es");

        String digit = String.valueOf(channel.waitForDigit(-1));
        log.debug("Confirmar/Corregir choice for {}: {}", callerId, digit);

        if ("1".equals(digit)) {
            Optional<ReservaResponseDTO> reservaConfirmada = reservaService.crearReserva(reservaRequest);
            if (reservaConfirmada.isPresent()) {
                ReservaResponseDTO reserva = reservaConfirmada.get();
                channel.exec("AGI", "googletts.agi,\"Su reserva ha sido confirmada con éxito.\",es");
                channel.exec("AGI", "googletts.agi,\"Número de reserva: " + reserva.getId() + ".\",es");
                channel.exec("AGI", "googletts.agi,\"Detalles: De " + reserva.getNombreDestinoOrigen() + " a " + reserva.getNombreDestinoLlegada() + ".\",es");
                channel.exec("AGI", "googletts.agi,\"El " + reserva.getFechaViaje().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")) + " a las " + reserva.getHoraViaje().format(DateTimeFormatter.ofPattern("HH 'horas' mm 'minutos'")) + ".\",es");
                channel.exec("AGI", "googletts.agi,\"Valor total: " + reserva.getValorTotal() + " pesos.\",es");
                channel.exec("AGI", "googletts.agi,\"Gracias por su reserva. Adiós.\",es");
                channel.hangup();
            } else {
                channel.exec("AGI", "googletts.agi,\"No fue posible confirmar su reserva. Por favor, intente de nuevo.\",es");
                mainMenu(channel, callerId); // Volver al menú principal o intentar de nuevo
            }
        } else if ("2".equals(digit)) {
            channel.exec("AGI", "googletts.agi,\"Volviendo a la selección de origen para corregir la reserva.\",es");
            seleccionarOrigen(channel, callerId); // Reiniciar el proceso de selección
        } else {
            channel.exec("AGI", "googletts.agi,\"Opción no válida. Volviendo al menú principal.\",es");
            mainMenu(channel, callerId);
        }
    }
}