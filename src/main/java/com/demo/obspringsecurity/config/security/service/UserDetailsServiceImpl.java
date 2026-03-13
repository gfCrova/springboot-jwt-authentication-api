package com.demo.obspringsecurity.config.security.service;

import com.demo.obspringsecurity.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Autentíca un usuario en la base de datos
 * <br>
 * Authentication Manager llama al metodo loadUserByUsername de esta clase
 * para obtener los detalles del usuario de la base de datos cuando
 * se intente autenticar un usuario
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // Este metodo busca un 'User' en la Base de Datos y lo retorna en el formato que Spring Security necesita para autenticarlo.
    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        // Busca el usuario, si no lo encuentra lanza una exception
        com.demo.obspringsecurity.domain.User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found with username: " + username)
        );
        // Retorna un UserDetails
        return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }
}