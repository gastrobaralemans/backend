package com.gastrobar_alemans_backend.repository;

import com.gastrobar_alemans_backend.model.Notis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface NotisRepository extends JpaRepository<Notis, Long> {

    @Query("SELECT n FROM Notis n WHERE n.receptor.correo = :correo ORDER BY n.fecha DESC")
    List<Notis> buscarPorCorreo(@Param("correo") String correo);
}
