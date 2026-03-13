package com.demo.obspringsecurity.config.security;

import com.demo.obspringsecurity.config.security.jwt.JwtAuthEntryPoint;
import com.demo.obspringsecurity.config.security.jwt.JwtRequestFilter;
import com.demo.obspringsecurity.config.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
/**
 * <p>Configuración principal de seguridad de la aplicación.</p>
 * Esta clase configura el sistema de autenticación y autorización utilizando Spring Security y JWT.
 *<ul>
 * Funciones principales:
 *<li> 1. Definir el encoder de contraseñas.</li>
 *<li> 2. Configurar el AuthenticationProvider que usa UserDetailsService.</li>
 *<li> 3. Configurar el AuthenticationManager.</li>
 *<li> 4. Definir la configuración global de CORS.</li>
 *<li> 5. Configurar el SecurityFilterChain con reglas de seguridad.</li>
 *<li> 6. Registrar el filtro JWT para validar tokens en cada request.</li>
 *</ul>
 * Esta configuración reemplaza el antiguo WebSecurityConfigurerAdapter que fue eliminado en versiones modernas de Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Servicio encargado de cargar usuarios desde la base de datos
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // Clase que maneja errores de autenticación (401 Unauthorized).
    @Autowired
    private JwtAuthEntryPoint  unauthorizedHandler;

    // Filtro que intercepta cada request para validar el token JWT.
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    // =========================== PASSWORD ENCODER =========================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ====================== AUTHENTICATION PROVIDER ================================

    /**
     * <p>Proveedor de autenticación que utiliza:</p>
     * <li>- UserDetailsService para cargar usuarios</li>
     * <li>- PasswordEncoder para verificar contraseñas</li>
     * */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // ========================== AUTHENTICATION MANAGER ===========================

    /**
    * Este manager es utilizado normalmente en el LoginController para autenticar usuarios durante el login.
    */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // =============================== CORS CONFIG ============================

    /**
     *<p>Configuración global de CORS para permitir requests desde aplicaciones frontend externas.</p>
     * <p>Ejemplo: Angular / React / Vue</p>
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(
                List.of("http://localhost:4200",
                        "https://angular-springboot1-beta.vercel.app"));
        configuration.setAllowedMethods(
                List.of("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
        configuration.setAllowedHeaders(
                List.of("Access-Control-Allow-Origin",
                        "X-Requested-With",
                        "Origin",
                        "Content-Type",
                        "Accept",
                        "Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // ============================= SECURITY FILTER CHAIN =====================================

    /**
     * <p>Configuración principal de seguridad de la aplicación.</p>
     * <br>
     * <ul><p>Define:</p>
         * <li> Configuración de CORS</li>
         * <li> Deshabilitación de CSRF</li>
         * <li> Manejo de errores de autenticación</li>
         * <li> Política de sesiones (stateless para JWT)</li>
         * <li> Endpoints públicos y protegidos</li>
         * <li> Registro del filtro JWT</li>
     * </ul>
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Habilitar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Deshabilitar CSRF para APIs REST
                .csrf(AbstractHttpConfigurer::disable)
                // Manejar errores de autenticación
                .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedHandler))
                // Sin sesiones (JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configuración de endPoints
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
                        .requestMatchers("/api/hello/**").authenticated()
                        .requestMatchers("/").permitAll()
                        .anyRequest().authenticated()
                ).authenticationProvider(authenticationProvider()); // registrar proveedor de autenticación
                //.formLogin(withDefaults())  // desde el Navegador
                //.httpBasic(withDefaults());  // desde Postman

        // Agregar filtro JWT antes del filtro de autenticación estándar
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
    @Bean
    public UserDetailsService users(PasswordEncoder passwordEncoder) {
        UserDetails admin = User
                .withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("ADMIN")
                .build();

        UserDetails user = User
                .withUsername("user")
                .password(passwordEncoder.encode("1234"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    Manipular el Firewall si necesitas usar caracteres en tu URL ya que Spring Security los bloquea por default
    @Bean
    public HttpFirewall looseHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        return firewall;
    }*/
}
