package com.gastrobar_alemans_backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gastrobar_alemans_backend.model.RegisterModel;
import com.gastrobar_alemans_backend.model.Person;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterController registerController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private RegisterModel validRegisterModel;
    private Person existingPerson;
    private Person newPerson;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(registerController).build();

        validRegisterModel = new RegisterModel();
        validRegisterModel.setNombre("Juan Pérez");
        validRegisterModel.setCorreo("juan@test.com");
        validRegisterModel.setPass("password123");

        existingPerson = new Person();
        existingPerson.setNombre("Juan Pérez");
        existingPerson.setCorreo("juan@test.com");
        existingPerson.setPass("encodedPassword");
        existingPerson.setRol("usuario");

        newPerson = new Person();
        newPerson.setNombre("Juan Pérez");
        newPerson.setCorreo("juan@test.com");
        newPerson.setPass("encodedPassword");
        newPerson.setRol("usuario");
    }

    @Test
    void testRegister_Success() throws Exception {
        when(personRepository.findByCorreo(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(personRepository.save(any(Person.class))).thenReturn(newPerson);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterModel)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registro exitoso"));

        verify(personRepository).findByCorreo("juan@test.com");
        verify(passwordEncoder).encode("password123");
        verify(personRepository).save(any(Person.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() throws Exception {
        when(personRepository.findByCorreo(anyString())).thenReturn(Optional.of(existingPerson));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Correo ya registrado"));

        verify(personRepository).findByCorreo("juan@test.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    void testRegister_WithNullName() throws Exception {
        RegisterModel invalidModel = new RegisterModel();
        invalidModel.setNombre(null);
        invalidModel.setCorreo("test@test.com");
        invalidModel.setPass("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidModel)))
                .andExpect(status().isBadRequest());

        verify(personRepository, never()).findByCorreo(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    void testRegister_WithEmptyName() throws Exception {
        RegisterModel invalidModel = new RegisterModel();
        invalidModel.setNombre("");
        invalidModel.setCorreo("test@test.com");
        invalidModel.setPass("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidModel)))
                .andExpect(status().isBadRequest());

        verify(personRepository, never()).findByCorreo(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    void testRegister_WithInvalidEmail() throws Exception {
        RegisterModel invalidModel = new RegisterModel();
        invalidModel.setNombre("Juan Pérez");
        invalidModel.setCorreo("invalid-email");
        invalidModel.setPass("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidModel)))
                .andExpect(status().isBadRequest());

        verify(personRepository, never()).findByCorreo(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    void testRegister_WithShortPassword() throws Exception {
        RegisterModel invalidModel = new RegisterModel();
        invalidModel.setNombre("Juan Pérez");
        invalidModel.setCorreo("test@test.com");
        invalidModel.setPass("123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidModel)))
                .andExpect(status().isBadRequest());

        verify(personRepository, never()).findByCorreo(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    void testRegister_WithNullPassword() throws Exception {
        RegisterModel invalidModel = new RegisterModel();
        invalidModel.setNombre("Juan Pérez");
        invalidModel.setCorreo("test@test.com");
        invalidModel.setPass(null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidModel)))
                .andExpect(status().isBadRequest());

        verify(personRepository, never()).findByCorreo(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    void testRegister_VerifyPasswordEncoding() throws Exception {
        when(personRepository.findByCorreo(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(personRepository.save(any(Person.class))).thenReturn(newPerson);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterModel)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registro exitoso"));

        verify(passwordEncoder).encode("password123");
        verify(personRepository).save(argThat(person ->
                "encodedPassword123".equals(person.getPass())
        ));
    }

    @Test
    void testRegister_VerifyUserRole() throws Exception {
        when(personRepository.findByCorreo(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(personRepository.save(any(Person.class))).thenReturn(newPerson);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterModel)))
                .andExpect(status().isOk());

        verify(personRepository).save(argThat(person ->
                "usuario".equals(person.getRol())
        ));
    }

    @Test
    void testRegister_WithDifferentUserData() throws Exception {
        RegisterModel differentUser = new RegisterModel();
        differentUser.setNombre("María García");
        differentUser.setCorreo("maria@test.com");
        differentUser.setPass("differentPassword456");

        when(personRepository.findByCorreo("maria@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("differentPassword456")).thenReturn("encodedDifferentPassword");
        when(personRepository.save(any(Person.class))).thenReturn(newPerson);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(differentUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registro exitoso"));

        verify(personRepository).findByCorreo("maria@test.com");
        verify(passwordEncoder).encode("differentPassword456");
        verify(personRepository).save(any(Person.class));
    }
}
