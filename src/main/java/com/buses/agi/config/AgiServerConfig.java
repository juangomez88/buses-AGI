package com.buses.agi.config;

import com.buses.agi.controller.AgiController;
import lombok.extern.slf4j.Slf4j;
import org.asteriskjava.fastagi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class AgiServerConfig {

    @Value("${agi.server.port:4573}")
    private int agiServerPort;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Configura e inicia el servidor AGI.
     */
    @Bean
    public AgiServer agiServer() {
        DefaultAgiServer agiServer = new DefaultAgiServer();

        // Configuramos el puerto
        agiServer.setPort(agiServerPort);

        // Estrategia de mapeo: usar el bean de AgiController gestionado por Spring
        agiServer.setMappingStrategy(new MappingStrategy() {
            @Override
            public AgiScript determineScript(AgiRequest request, AgiChannel channel) {
                try {
                    return applicationContext.getBean(AgiController.class);
                } catch (Exception e) {
                    log.error("Error al obtener el bean AgiController desde Spring: {}", e.getMessage(), e);
                    return null;
                }
            }
        });

        // Iniciamos el servidor AGI en un hilo aparte
        Thread agiServerThread = new Thread(() -> {
            try {
                log.info("Iniciando servidor AGI en el puerto {}", agiServerPort);
                agiServer.startup();
            } catch (IOException e) {
                log.error("Error al iniciar el servidor AGI: {}", e.getMessage(), e);
            }
        }, "AgiServerThread");

        agiServerThread.setDaemon(false);
        agiServerThread.start();

        return agiServer;
    }
}
