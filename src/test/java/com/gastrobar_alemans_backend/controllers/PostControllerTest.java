package com.gastrobar_alemans_backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gastrobar_alemans_backend.model.Comentario;
import com.gastrobar_alemans_backend.model.Person;
import com.gastrobar_alemans_backend.model.Post;
import com.gastrobar_alemans_backend.repository.ComentarioRepository;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import com.gastrobar_alemans_backend.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PostController postController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Post post;
    private Person usuario;
    private Comentario comentario;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();

        post = new Post("Título del Post", "imagen.jpg", "Descripción del post");
        usuario = new Person();
        usuario.setNombre("Usuario Test");
        usuario.setCorreo("usuario@test.com");
        comentario = new Comentario("Contenido del comentario", usuario, post);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupSecurityContext(String username, String role) {
        // Configurar el contexto de seguridad
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                username, "password", authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testCrearPost_Success() throws Exception {
        // Configurar como ADMIN
        setupSecurityContext("admin@test.com", "ROLE_ADMIN");

        Map<String, String> data = new HashMap<>();
        data.put("titulo", "Nuevo Post");
        data.put("imagen", "nueva_imagen.jpg");
        data.put("descripcion", "Descripción del nuevo post");

        when(postRepository.save(any(Post.class))).thenReturn(post);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(content().string("Post creado correctamente"));

        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testCrearPost_UnauthorizedUser() throws Exception {
        // Configurar como USER (no ADMIN)
        setupSecurityContext("user@test.com", "ROLE_USER");

        Map<String, String> data = new HashMap<>();
        data.put("titulo", "Nuevo Post");
        data.put("imagen", "imagen.jpg");
        data.put("descripcion", "Descripción");

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isForbidden()) // Ahora debería retornar 403
                .andExpect(content().string("No tienes permisos para crear posts"));

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testCrearPost_UnauthorizedMesero() throws Exception {
        // Configurar como MESERO (no ADMIN)
        setupSecurityContext("mesero@test.com", "ROLE_MESERO");

        Map<String, String> data = new HashMap<>();
        data.put("titulo", "Nuevo Post");
        data.put("imagen", "imagen.jpg");
        data.put("descripcion", "Descripción");

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("No tienes permisos para crear posts"));

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testCrearPost_NotAuthenticated() throws Exception {
        // No configurar seguridad (usuario no autenticado)
        SecurityContextHolder.clearContext();

        Map<String, String> data = new HashMap<>();
        data.put("titulo", "Nuevo Post");
        data.put("imagen", "imagen.jpg");
        data.put("descripcion", "Descripción");

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Usuario no autenticado"));

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testListarPosts_Success() throws Exception {
        Post post2 = new Post("Segundo Post", "imagen2.jpg", "Segunda descripción");
        List<Post> posts = Arrays.asList(post, post2);

        when(postRepository.findAll()).thenReturn(posts);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Título del Post"))
                .andExpect(jsonPath("$[1].titulo").value("Segundo Post"));

        verify(postRepository).findAll();
    }

    @Test
    void testListarPosts_Empty() throws Exception {
        when(postRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(postRepository).findAll();
    }

    @Test
    void testListarComentarios_Success() throws Exception {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Comentario comentario1 = new Comentario("Comentario 1", usuario, post);
        Comentario comentario2 = new Comentario("Comentario 2", usuario, post);
        List<Comentario> comentarios = Arrays.asList(comentario1, comentario2);

        when(comentarioRepository.findByPostOrderByFechaDesc(post)).thenReturn(comentarios);

        mockMvc.perform(get("/api/posts/1/comentarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].contenido").value("Comentario 1"))
                .andExpect(jsonPath("$[1].contenido").value("Comentario 2"))
                .andExpect(jsonPath("$[0].usuarioNombre").value("Usuario Test"))
                .andExpect(jsonPath("$[1].usuarioNombre").value("Usuario Test"));

        verify(postRepository).findById(1L);
        verify(comentarioRepository).findByPostOrderByFechaDesc(post);
    }

    @Test
    void testListarComentarios_PostNotFound() throws Exception {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/posts/99/comentarios"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Post no encontrado"));

        verify(postRepository).findById(99L);
        verify(comentarioRepository, never()).findByPostOrderByFechaDesc(any());
    }

    @Test
    void testListarComentarios_EmptyComments() throws Exception {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(comentarioRepository.findByPostOrderByFechaDesc(post)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/posts/1/comentarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(postRepository).findById(1L);
        verify(comentarioRepository).findByPostOrderByFechaDesc(post);
    }

    @Test
    void testCrearComentario_Success() throws Exception {
        setupSecurityContext("user@test.com", "ROLE_USER");

        when(personRepository.findByCorreo("user@test.com")).thenReturn(Optional.of(usuario));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentario);

        Map<String, String> data = new HashMap<>();
        data.put("contenido", "Nuevo comentario");

        mockMvc.perform(post("/api/posts/1/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(content().string("Comentario creado correctamente"));

        verify(personRepository).findByCorreo("user@test.com");
        verify(postRepository).findById(1L);
        verify(comentarioRepository).save(any(Comentario.class));
    }

    @Test
    void testCrearComentario_UserNotFound() throws Exception {
        setupSecurityContext("user@test.com", "ROLE_USER");

        when(personRepository.findByCorreo("user@test.com")).thenReturn(Optional.empty());

        Map<String, String> data = new HashMap<>();
        data.put("contenido", "Nuevo comentario");

        mockMvc.perform(post("/api/posts/1/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Usuario no autorizado"));

        verify(personRepository).findByCorreo("user@test.com");
        verify(postRepository, never()).findById(any());
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    void testCrearComentario_PostNotFound() throws Exception {
        setupSecurityContext("user@test.com", "ROLE_USER");

        when(personRepository.findByCorreo("user@test.com")).thenReturn(Optional.of(usuario));
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        Map<String, String> data = new HashMap<>();
        data.put("contenido", "Nuevo comentario");

        mockMvc.perform(post("/api/posts/99/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Post no encontrado"));

        verify(personRepository).findByCorreo("user@test.com");
        verify(postRepository).findById(99L);
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    void testCrearComentario_WithEmptyContent() throws Exception {
        setupSecurityContext("user@test.com", "ROLE_USER");

        when(personRepository.findByCorreo("user@test.com")).thenReturn(Optional.of(usuario));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentario);

        Map<String, String> data = new HashMap<>();
        data.put("contenido", "");

        mockMvc.perform(post("/api/posts/1/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(content().string("Comentario creado correctamente"));

        verify(comentarioRepository).save(any(Comentario.class));
    }

    @Test
    void testCrearComentario_Unauthorized() throws Exception {
        // Configurar sin autenticación (usuario no autenticado)
        SecurityContextHolder.clearContext();

        Map<String, String> data = new HashMap<>();
        data.put("contenido", "Nuevo comentario");

        mockMvc.perform(post("/api/posts/1/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Usuario no autorizado"));

        verify(personRepository, never()).findByCorreo(any());
        verify(postRepository, never()).findById(any());
        verify(comentarioRepository, never()).save(any());
    }
}