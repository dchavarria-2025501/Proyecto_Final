package com.dchavarria.proyecto_final.repository;

import com.dchavarria.proyecto_final.entity.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    boolean existsByIsbn(String isbn);

    Page<Libro> findByTituloContainingIgnoreCaseOrCategoriaContainingIgnoreCase(
            String titulo, String categoria, Pageable pageable
    );
}