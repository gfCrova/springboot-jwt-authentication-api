package com.demo.obspringsecurity.config.security.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * Métodos para generar y validar token JWT
 */

@Component
public class JwtTokenUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("openb")
    private String jwtSecret;  // clave secreta usada para firmar el token

    @Value("86400000")
    private int jstExpirationMs; // tiempo de expiración del token (86400000ms = 24hs)


    /**
     * Este metodo crea un JWT cuando el usuario inicia sesión correctamente.
     * <ul>
     * Aquí se define el contenido del token:
     * <li> subject → username del usuario</li>
     * <li> issuedAt → fecha de creación</li>
     * <li> expiration → fecha de expiración</li>
     * <li> signWith → firma criptográfica</li>
     * La firma sirve para que el servidor pueda verificar que el token no fue modificado.
     * </ul>
     */
    public String generateJwtToken(Authentication authentication){

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((Objects.requireNonNull(userPrincipal).getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jstExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Este metodo extrae el username del token.
     * <ul>
     *    <li>Se verifica la firma del token</li>
     *    <li>Se leen los claims (datos dentro del token)</li>
     *    <li>Se obtiene el subject (username)</li>
     * </ul>
     */
    public String getUserNameFromJwtToken(String token){

        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Este metodo verifica que el token sea válido.
     * <ul>
     *     Si el token es válido → devuelve true. Si ocurre un error → captura excepciones:
     *     <li>SignatureException → firma incorrecta</li>
     *     <li>MalformedJwtException → token mal formado</li>
     *     <li>ExpiredJwtException → token expirado</li>
     *     <li>UnsupportedJwtException → formato no soportado</li>
     *     <li>IllegalArgumentException → token vacío</li>
     * </ul>
     * */

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
