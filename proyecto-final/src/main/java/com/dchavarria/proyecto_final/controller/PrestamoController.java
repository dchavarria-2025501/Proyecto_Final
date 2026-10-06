package com.dchavarria.proyecto_final.controller;

import com.dchavarria.proyecto_final.dto.PrestamoRequest;
import com.dchavarria.proyecto_final.dto.PrestamoResponseDTO;
import com.dchavarria.proyecto_final.service.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @PostMapping
    public ResponseEntity<PrestamoResponseDTO> registrarPrestamo(@Valid @RequestBody PrestamoRequest request) {
        return new ResponseEntity<>(prestamoService.registrarPrestamo(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/devolucion")
    public ResponseEntity<PrestamoResponseDTO> registrarDevolucion(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.registrarDevolucion(id));
    }

    @GetMapping("/mis-prestamos")
    public ResponseEntity<List<PrestamoResponseDTO>> obtenerMisPrestamos(Authentication authentication) {
        return ResponseEntity.ok(prestamoService.obtenerMisPrestamos(authentication.getName()));
    }

    @GetMapping("/atrasados")
    public ResponseEntity<List<PrestamoResponseDTO>> obtenerPrestamosAtrasados() {
        return ResponseEntity.ok(prestamoService.obtenerPrestamosAtrasados());
    }
}