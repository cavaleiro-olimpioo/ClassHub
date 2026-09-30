package com.classhub.api.Controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST simples usado para verificação de disponibilidade
 * (health check) da API, útil para monitoramento e orquestradores
 * (ex.: Docker, Kubernetes).
 */
@RestController
public class HealthController {
    /**
     * Endpoint de verificação de saúde da aplicação.
     *
     * @return mapa contendo o status atual da aplicação (sempre {@code "UP"} se o serviço responder)
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
