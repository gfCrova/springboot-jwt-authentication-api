package com.demo.obspringsecurity;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;


public class EncryptionTest {

    /**
     * BCrypt genera su propio salt de 16 bytes<p>
     * El resultado de cifrar bcrypt será un string de 60 caracteres<p>
     * - $a versión <p>
     * - $10 fuerza (valor que va de 4 a 31, por defecto vale 10)<p>
     * - Los 22 siguientes caracteres son el salt generado<p>
     */
    @Test
    void bcryptTest(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // encode() -> Cifra el String
        String passBcrypt = passwordEncoder.encode("password");
        System.out.println(passBcrypt);

        // matches() -> Verifica si el string dado es igual al cifrado y devuelve boolean
        boolean result = passwordEncoder.matches("password", passBcrypt);
        System.out.println(result);
    }

    // CIFRADO: pbkdf2
    @Test
    void pbkdf2Test(){
        Pbkdf2PasswordEncoder passwordEncoder = new Pbkdf2PasswordEncoder("password", 22, 11, 33);
        System.out.println(passwordEncoder.encode("password"));
    }

    // CIFRADO: argon2
    @Test
    void argon2Test(){
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(16, 32, 1, 60000, 10);
        System.out.println(passwordEncoder.encode("password"));
    }

    // CIFRADO: scrypt
    @Test
    void scryptTest(){
        SCryptPasswordEncoder passwordEncoder = new SCryptPasswordEncoder(16, 32, 1, 50, 10);
        System.out.println(passwordEncoder.encode("password"));
    }

    @Test
    void testEncoders(){
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder("password", 22, 11, 33));
        encoders.put("argon2", new Argon2PasswordEncoder(16, 32, 1, 60000, 10));
        encoders.put("scrypt", new SCryptPasswordEncoder(16, 32, 1, 50, 10));
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        encoders.put("sha256", new StandardPasswordEncoder());

        // Delegar el cifrado al especificado en el primer parámetro
        PasswordEncoder passwordEncoders = new DelegatingPasswordEncoder("pbkdf2", encoders);
        String hashedPassword = passwordEncoders.encode("password");
        System.out.println(hashedPassword);
    }
}
