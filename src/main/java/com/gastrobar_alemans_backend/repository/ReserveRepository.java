package com.gastrobar_alemans_backend.repository;

import com.gastrobar_alemans_backend.model.Reserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve, Long> {
    List<Reserve> findByCorreoAndEstadoIn(String correo, List<String> pendiente);
    boolean existsByNumeroAndEstadoIn(String numero, List<String> estados);

}