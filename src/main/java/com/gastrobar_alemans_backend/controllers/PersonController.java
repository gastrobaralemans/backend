package com.gastrobar_alemans_backend.controllers;
import com.gastrobar_alemans_backend.model.Person;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/personas")
@CrossOrigin(origins = "http://localhost:5173")
public class PersonController {
    @Autowired
    private PersonRepository personRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Map<String, Object>> listarPersonas() {
        return personRepository.findAll().stream()
                .map(person -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", person.getId());
                    map.put("nombre", person.getNombre());
                    map.put("correo", person.getCorreo());
                    map.put("rol", person.getRol());
                    return map;
                })
                .collect(Collectors.toList());
    }

}
