package com.gastrobar_alemans_backend.repository;

import com.gastrobar_alemans_backend.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByCorreo(String correo);
}
