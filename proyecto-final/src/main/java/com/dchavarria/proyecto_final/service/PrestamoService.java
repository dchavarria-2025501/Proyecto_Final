package com.dchavarria.proyecto_final.service;

import com.dchavarria.proyecto_final.dto.PrestamoRequest;
import com.dchavarria.proyecto_final.dto.PrestamoResponseDTO;
import com.dchavarria.proyecto_final.entity.Libro;
import com.dchavarria.proyecto_final.entity.Prestamo;
import com.dchavarria.proyecto_final.entity.Usuario;
import com.dchavarria.proyecto_final.enums.EstadoPrestamo;
import com.dchavarria.proyecto_final.enums.EstadoUsuario;
import com.dchavarria.proyecto_final.exception.BusinessRuleException;
import com.dchavarria.proyecto_final.exception.ResourceNotFoundException;
import com.dchavarria.proyecto_final.repository.LibroRepository;
import com.dchavarria.proyecto_final.repository.PrestamoRepository;
import com.dchavarria.proyecto_final.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Transactional
    public PrestamoResponseDTO registrarPrestamo(PrestamoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Libro libro = libroRepository.findById(request.getLibroId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));

        // Regla 1: Validar sanción previa por fecha de entrega vencida
        List<Prestamo> prestamosActivos = prestamoRepository.findByUsuarioId(usuario.getId());
        boolean tieneAtrasados = prestamosActivos.stream()
                .anyMatch(p -> p.getEstado() == EstadoPrestamo.ACTIVO && LocalDate.now().isAfter(p.getFechaDevolucionEsperada()));

        if (tieneAtrasados || usuario.getEstado() == EstadoUsuario.SANCIONADO) {
            usuario.setEstado(EstadoUsuario.SANCIONADO);
            usuarioRepository.save(usuario);
            throw new BusinessRuleException("El usuario está SANCIONADO por préstamos atrasados");
        }

        // Regla 2: Máximo 3 préstamos activos
        long prestamosVigentes = prestamoRepository.countByUsuarioIdAndEstado(usuario.getId(), EstadoPrestamo.ACTIVO);
        if (prestamosVigentes >= 3) {
            throw new BusinessRuleException("El usuario ya tiene 3 préstamos activos simultáneos");
        }

        // Regla 3: Validar stock disponible
        if (libro.getStockDisponible() <= 0) {
            throw new BusinessRuleException("No hay ejemplares disponibles para este libro");
        }

        // Descontar stock
        libro.setStockDisponible(libro.getStockDisponible() - 1);
        libroRepository.save(libro);

        // Registrar préstamo (14 días de plazo)
        Prestamo prestamo = Prestamo.builder()
                .usuario(usuario)
                .libro(libro)
                .fechaPrestamo(LocalDate.now())
                .fechaDevolucionEsperada(LocalDate.now().plusDays(14))
                .estado(EstadoPrestamo.ACTIVO)
                .build();

        return mapToDTO(prestamoRepository.save(prestamo));
    }

    @Transactional
    public PrestamoResponseDTO registrarDevolucion(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado"));

        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new BusinessRuleException("El préstamo ya fue devuelto anteriormente");
        }

        prestamo.setFechaDevolucionReal(LocalDate.now());
        if (LocalDate.now().isAfter(prestamo.getFechaDevolucionEsperada())) {
            prestamo.setEstado(EstadoPrestamo.ATRASADO);
        } else {
            prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        }

        // Reincorporar stock
        Libro libro = prestamo.getLibro();
        libro.setStockDisponible(libro.getStockDisponible() + 1);
        libroRepository.save(libro);

        return mapToDTO(prestamoRepository.save(prestamo));
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> obtenerMisPrestamos(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return prestamoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> obtenerPrestamosAtrasados() {
        return prestamoRepository.findByEstadoAndFechaDevolucionEsperadaBefore(EstadoPrestamo.ACTIVO, LocalDate.now())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PrestamoResponseDTO mapToDTO(Prestamo prestamo) {
        PrestamoResponseDTO dto = new PrestamoResponseDTO();
        dto.setId(prestamo.getId());
        dto.setUsuarioEmail(prestamo.getUsuario().getEmail());
        dto.setLibroTitulo(prestamo.getLibro().getTitulo());
        dto.setFechaPrestamo(prestamo.getFechaPrestamo());
        dto.setFechaDevolucionEsperada(prestamo.getFechaDevolucionEsperada());
        dto.setFechaDevolucionReal(prestamo.getFechaDevolucionReal());
        dto.setEstado(prestamo.getEstado());
        return dto;
    }
}