package com.dchavarria.proyecto_final;

import com.dchavarria.proyecto_final.entity.Usuario;
import com.dchavarria.proyecto_final.enums.EstadoUsuario;
import com.dchavarria.proyecto_final.enums.Rol;
import com.dchavarria.proyecto_final.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootApplication
public class ProyectoFinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoFinalApplication.class, args);
    }

    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!usuarioRepository.existsByEmail("admin@biblioteca.com")) {
                Usuario admin = Usuario.builder()
                        .nombre("Administrador Principal")
                        .email("admin@biblioteca.com")
                        .password(passwordEncoder.encode("Admin123*"))
                        .estado(EstadoUsuario.ACTIVO)
                        .rol(Rol.ADMIN)
                        .build();
                usuarioRepository.save(admin);
                System.out.println(">>> Usuario ADMIN creado: admin@biblioteca.com / Admin123*");
            }

               /* if (!usuarioRepository.existsByEmail("lector@biblioteca.com")) {
                Usuario bibliotecario = Usuario.builder()
                        .nombre("Bibliotecario Turno Mañana")
                        .email("lector@biblioteca.com")
                        .password(passwordEncoder.encode("Lector123*"))
                        .estado(EstadoUsuario.ACTIVO)
                        .rol(Rol.BIBLIOTECARIO)
                        .build();
                usuarioRepository.save(bibliotecario);
                System.out.println(">>> Usuario BIBLIOTECARIO creado: lector@biblioteca.com / Lector123*");
            }
                */
        };
    }
}