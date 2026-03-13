package com.demo.obspringsecurity.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.SignatureException;
import java.util.Date;

/**
 * Métodos para generar y validar token JWT
 */

@Component
public class JwtTokenUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;  // clave secreta usada para firmar el token

    @Value("${jwt.expiration}")
    private int jwtExpirationMs; // tiempo de expiración del token (86400000ms = 24hs)


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

        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        assert userPrincipal != null;
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
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

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
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
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (JwtException e) {
            log.error("Invalid JWT: {}", e.getMessage());
          }
    return false;
    }
}
