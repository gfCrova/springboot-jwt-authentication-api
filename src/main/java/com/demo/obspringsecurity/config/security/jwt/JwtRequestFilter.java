package com.demo.obspringsecurity.config.security.jwt;

import com.demo.obspringsecurity.config.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * <li>Filtro que intercepta cada request HTTP para verificar si contiene un JWT válido.</li>
 * <li>De esta manera Spring Security reconoce al usuario como autenticado durante el procesamiento de la request.</li>
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public static final String BEARER = "Bearer ";

    /**
     * <li>Metodo que se ejecuta una vez por cada request HTTP.</li>
     * <li>Aquí se realiza el proceso de validación del JWT.</li>
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            // Paso 1: extraer el token JWT desde el header Authorization
            String jwt = parseJwt(request);
            // Paso 2: validar el token
            if (jwt != null && jwtTokenUtil.validateJwtToken(jwt)) {
                // Paso 3: obtener el username almacenado dentro del token
                String username = jwtTokenUtil.getUserNameFromJwtToken(jwt);
                // Paso 4: cargar el usuario desde la base de datos
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // Paso 5: crear el objeto de autenticación para Spring Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                // Paso 6: guardar la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }catch (Exception e){
            logger.error("Cannot set user authentication: {}", e);
        }
        // Paso 7: continuar con el resto de filtros de la cadena
        filterChain.doFilter(request, response);
    }

    /**
     * <p>Extrae el token JWT del header Authorization.</p>
     *<br>
     * Ejemplo de header:
     * <li>Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...</li>
     *
     * @param request request HTTP entrante
     * @return el token JWT sin el prefijo "Bearer", o null si no existe
     */
    private String parseJwt(HttpServletRequest request) {
        // Obtener el header Authorization
        String headerAuth = request.getHeader("Authorization");
        // Verificar que exista y que comience con "BEARER"
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER))
            return headerAuth.substring(BEARER.length());

        return null;
    }
}
