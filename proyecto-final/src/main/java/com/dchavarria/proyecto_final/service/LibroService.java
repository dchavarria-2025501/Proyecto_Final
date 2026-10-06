package com.dchavarria.proyecto_final.service;

import com.dchavarria.proyecto_final.dto.LibroDTO;
import com.dchavarria.proyecto_final.entity.Libro;
import com.dchavarria.proyecto_final.exception.BusinessRuleException;
import com.dchavarria.proyecto_final.exception.ResourceNotFoundException;
import com.dchavarria.proyecto_final.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Transactional(readOnly = true)
    public Page<LibroDTO> listarLibros(String filtro, Pageable pageable) {
        Page<Libro> libros;
        if (filtro != null && !filtro.trim().isEmpty()) {
            libros = libroRepository.findByTituloContainingIgnoreCaseOrCategoriaContainingIgnoreCase(filtro, filtro, pageable);
        } else {
            libros = libroRepository.findAll(pageable);
        }
        return libros.map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public LibroDTO obtenerPorId(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));
        return mapToDTO(libro);
    }

    @Transactional
    public LibroDTO crear(LibroDTO dto) {
        if (libroRepository.existsByIsbn(dto.getIsbn())) {
            throw new BusinessRuleException("El ISBN ya existe en el sistema");
        }

        Libro libro = Libro.builder()
                .isbn(dto.getIsbn())
                .titulo(dto.getTitulo())
                .autor(dto.getAutor())
                .categoria(dto.getCategoria())
                .stockTotal(dto.getStockTotal())
                .stockDisponible(dto.getStockTotal())
                .build();

        return mapToDTO(libroRepository.save(libro));
    }

    @Transactional
    public LibroDTO actualizar(Long id, LibroDTO dto) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));

        libro.setTitulo(dto.getTitulo());
        libro.setAutor(dto.getAutor());
        libro.setCategoria(dto.getCategoria());
        if (dto.getStockTotal() != null) {
            int diferencia = dto.getStockTotal() - libro.getStockTotal();
            libro.setStockTotal(dto.getStockTotal());
            libro.setStockDisponible(libro.getStockDisponible() + diferencia);
        }

        return mapToDTO(libroRepository.save(libro));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Libro no encontrado con ID: " + id);
        }
        libroRepository.deleteById(id);
    }

    private LibroDTO mapToDTO(Libro libro) {
        LibroDTO dto = new LibroDTO();
        dto.setId(libro.getId());
        dto.setIsbn(libro.getIsbn());
        dto.setTitulo(libro.getTitulo());
        dto.setAutor(libro.getAutor());
        dto.setCategoria(libro.getCategoria());
        dto.setStockTotal(libro.getStockTotal());
        dto.setStockDisponible(libro.getStockDisponible());
        return dto;
    }
}