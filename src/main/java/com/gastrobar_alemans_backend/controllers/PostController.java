package com.gastrobar_alemans_backend.controllers;
import com.gastrobar_alemans_backend.model.Comentario;
import com.gastrobar_alemans_backend.model.Person;
import com.gastrobar_alemans_backend.model.Post;
import com.gastrobar_alemans_backend.repository.ComentarioRepository;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import com.gastrobar_alemans_backend.repository.PostRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173")
public class PostController {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private PersonRepository personRepository;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> crearPost(@Valid @RequestBody Map<String, String> data) {
        String titulo = data.get("titulo");
        String imagen = data.get("imagen");
        String descripcion = data.get("descripcion");

        Post post = new Post(titulo, imagen, descripcion);
        postRepository.save(post);
        return ResponseEntity.ok("Post creado correctamente");
    }
    @GetMapping
    public List<Post> listarPosts() {
        return postRepository.findAll();
    }
    @GetMapping("/{id}/comentarios")
    public ResponseEntity<?> listarComentarios(@PathVariable Long id) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Post no encontrado");
        }
        List<Comentario> comentarios = comentarioRepository.findByPostOrderByFechaDesc(postOpt.get());
        List<Map<String, Object>> response = new ArrayList<>();
        for (Comentario c : comentarios) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("contenido", c.getContenido());
            map.put("usuarioNombre", c.getUsuario().getNombre());
            map.put("fecha", c.getFecha());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{id}/comentarios")
    public ResponseEntity<?> crearComentario(@PathVariable Long id, @RequestBody Map<String, String> data) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        Optional<Person> usuarioOpt = personRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(403).body("Usuario no autorizado");
        }

        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Post no encontrado");
        }

        String contenido = data.get("contenido");
        Comentario comentario = new Comentario(contenido, usuarioOpt.get(), postOpt.get());
        comentarioRepository.save(comentario);

        return ResponseEntity.ok("Comentario creado correctamente");
    }

}

