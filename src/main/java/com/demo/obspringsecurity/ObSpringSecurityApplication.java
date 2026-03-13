package com.demo.obspringsecurity;

import com.demo.obspringsecurity.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ObSpringSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObSpringSecurityApplication.class, args);

        //UserRepository repository = context.getBean(UserRepository.class);
        //BCryptPasswordEncoder encoder = context.getBean(BCryptPasswordEncoder.class);
        //User usuario1 = new User("Marcos", encoder.encode("password1234"));
        //repository.save(usuario1);

    }
}
