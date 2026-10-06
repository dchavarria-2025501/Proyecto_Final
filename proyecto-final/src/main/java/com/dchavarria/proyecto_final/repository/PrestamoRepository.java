package com.dchavarria.proyecto_final.repository;

import com.dchavarria.proyecto_final.entity.Prestamo;
import com.dchavarria.proyecto_final.enums.EstadoPrestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    long countByUsuarioIdAndEstado(Long usuarioId, EstadoPrestamo estado);

    List<Prestamo> findByUsuarioId(Long usuarioId);

    List<Prestamo> findByEstadoAndFechaDevolucionEsperadaBefore(
            EstadoPrestamo estado, LocalDate fecha
    );
}