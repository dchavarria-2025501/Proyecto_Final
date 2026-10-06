package com.dchavarria.proyecto_final.service;

import com.dchavarria.proyecto_final.dto.AuthResponse;
import com.dchavarria.proyecto_final.dto.LoginRequest;
import com.dchavarria.proyecto_final.dto.RegisterRequest;
import com.dchavarria.proyecto_final.entity.Usuario;
import com.dchavarria.proyecto_final.enums.EstadoUsuario;
import com.dchavarria.proyecto_final.enums.Rol;
import com.dchavarria.proyecto_final.exception.BusinessRuleException;
import com.dchavarria.proyecto_final.repository.UsuarioRepository;
import com.dchavarria.proyecto_final.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("El email ya se encuentra registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .estado(EstadoUsuario.ACTIVO)
                .rol(Rol.LECTOR)
                .build();

        usuarioRepository.save(usuario);
        String token = jwtUtils.generateToken(usuario.getEmail(), usuario.getRol().name());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessRuleException("Credenciales inválidas"));

        String token = jwtUtils.generateToken(usuario.getEmail(), usuario.getRol().name());
        return new AuthResponse(token);
    }
}