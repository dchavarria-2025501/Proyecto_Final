package com.dchavarria.proyecto_final.dto;

import lombok.Data;

@Data
public class LibroDTO {
    private Long id;
    private String isbn;
    private String titulo;
    private String autor;
    private String categoria;
    private Integer stockTotal;
    private Integer stockDisponible;
}