package com.demo.obspringsecurity.config.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <p>Punto de entrada de autenticación para Spring Security.</p>
 *
 * <p>Esta clase se ejecuta cuando un usuario intenta acceder a un recurso
 * protegido sin estar autenticado o con un JWT inválido.</p>
 *
 * <p>Su función es devolver una respuesta HTTP 401 (Unauthorized).</p>
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

    /**
     * <p>Este metodo es invocado automáticamente por Spring Security cuando ocurre un error de autenticación.</p>
     * <ul>Ejemplos de situaciones donde se ejecuta:
         * <li> No se envía token JWT</li>
         * <li> El token es inválido</li>
         * <li> El token está expirado</li>
         * <li> El usuario no está autenticado</li>
     * </ul>
     * @param request request HTTP que originó el error
     * @param response response HTTP que se enviará al cliente
     * @param authException excepción de autenticación generada
     */
    @Override
    public void commence(@NonNull HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("Unauthorized error: {}", authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}
