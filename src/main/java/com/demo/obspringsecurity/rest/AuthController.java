package com.demo.obspringsecurity.rest;

import com.demo.obspringsecurity.config.security.jwt.JwtTokenUtil;
import com.demo.obspringsecurity.config.security.payload.JwtResponse;
import com.demo.obspringsecurity.config.security.payload.LoginRequest;
import com.demo.obspringsecurity.config.security.payload.MessageResponse;
import com.demo.obspringsecurity.config.security.payload.RegisterRequest;
import com.demo.obspringsecurity.domain.User;
import com.demo.obspringsecurity.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * <h5>Controller encargado de la autenticación de usuarios.</h5>
 * <ul>Endpoints disponibles:
 *     <li>POST /api/auth/login   (Autentica un usuario y devuelve un JWT.)</li>
 *     <li>POST /api/auth/register   (Registra un nuevo usuario en la base de datos.)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Manager encargado de autenticar usuarios.
    private final AuthenticationManager authManager;
    // Repositorio para acceder a usuarios en la base de datos.
    private final UserRepository userRepository;
    // Encoder utilizado para cifrar contraseñas.
    private final PasswordEncoder encoder;
    //  Utilidad para generar tokens JWT.
    private final JwtTokenUtil jwtTokenUtil;

    public AuthController(AuthenticationManager authManager,
                          UserRepository userRepository,
                          PasswordEncoder encoder,
                          JwtTokenUtil jwtTokenUtil) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * <h5>Endpoint de login.</h5>
     *
     * Autentica un usuario y genera un token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest  loginRequest) {

        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenUtil.generateJwtToken(authentication);

        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    /**
     * <h5>Endpoint de registro de usuarios.</h5>
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody RegisterRequest registerRequest) {
        // Verifica el username
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        // Verifica el email
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        // Crea el usuario
        User user = new User(registerRequest.getUsername(),
                registerRequest.getEmail(),
                encoder.encode(registerRequest.getPassword()));
        // Guarda el usuario
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Register Successfully!"));
    }
}
