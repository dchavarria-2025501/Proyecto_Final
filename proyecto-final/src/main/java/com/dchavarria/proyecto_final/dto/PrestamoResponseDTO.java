package com.dchavarria.proyecto_final.dto;

import com.dchavarria.proyecto_final.enums.EstadoPrestamo;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PrestamoResponseDTO {
    private Long id;
    private String usuarioEmail;
    private String libroTitulo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEsperada;
    private LocalDate fechaDevolucionReal;
    private EstadoPrestamo estado;
}