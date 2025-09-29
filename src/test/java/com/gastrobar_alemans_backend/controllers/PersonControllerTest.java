package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.model.Person;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonControllerTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonController personController;

    private Person adminPerson;
    private Person meseroPerson;
    private Person clientePerson;

    @BeforeEach
    void setUp() throws Exception {
        adminPerson = createPersonWithId(1L, "Admin User", "admin@test.com", "ADMIN");
        meseroPerson = createPersonWithId(2L, "Mesero User", "mesero@test.com", "MESERO");
        clientePerson = createPersonWithId(3L, "Cliente User", "cliente@test.com", "CLIENTE");
    }

    private Person createPersonWithId(Long id, String nombre, String correo, String rol) throws Exception {
        Person person = new Person();
        person.setNombre(nombre);
        person.setCorreo(correo);
        person.setRol(rol);
        person.setPass("password123");

        // Usar reflection para asignar el ID
        Field idField = Person.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(person, id);

        return person;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListarPersonas_Success() {
        List<Person> personas = Arrays.asList(adminPerson, meseroPerson, clientePerson);
        when(personRepository.findAll()).thenReturn(personas);

        List<Map<String, Object>> result = personController.listarPersonas();

        assertEquals(3, result.size());

        Map<String, Object> adminMap = result.get(0);
        assertEquals(1L, adminMap.get("id"));
        assertEquals("Admin User", adminMap.get("nombre"));
        assertEquals("admin@test.com", adminMap.get("correo"));
        assertEquals("ADMIN", adminMap.get("rol"));

        Map<String, Object> meseroMap = result.get(1);
        assertEquals(2L, meseroMap.get("id"));
        assertEquals("Mesero User", meseroMap.get("nombre"));
        assertEquals("mesero@test.com", meseroMap.get("correo"));
        assertEquals("MESERO", meseroMap.get("rol"));

        Map<String, Object> clienteMap = result.get(2);
        assertEquals(3L, clienteMap.get("id"));
        assertEquals("Cliente User", clienteMap.get("nombre"));
        assertEquals("cliente@test.com", clienteMap.get("correo"));
        assertEquals("CLIENTE", clienteMap.get("rol"));

        verify(personRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListarPersonas_EmptyList() {
        when(personRepository.findAll()).thenReturn(Arrays.asList());

        List<Map<String, Object>> result = personController.listarPersonas();

        assertTrue(result.isEmpty());
        verify(personRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListarPersonas_SinglePerson() {
        when(personRepository.findAll()).thenReturn(Arrays.asList(adminPerson));

        List<Map<String, Object>> result = personController.listarPersonas();

        assertEquals(1, result.size());

        Map<String, Object> personMap = result.get(0);
        assertEquals(1L, personMap.get("id"));
        assertEquals("Admin User", personMap.get("nombre"));
        assertEquals("admin@test.com", personMap.get("correo"));
        assertEquals("ADMIN", personMap.get("rol"));

        verify(personRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListarPersonas_WithNullFields() throws Exception {
        Person personWithNulls = createPersonWithId(4L, null, "test@test.com", null);

        when(personRepository.findAll()).thenReturn(Arrays.asList(personWithNulls));

        List<Map<String, Object>> result = personController.listarPersonas();

        assertEquals(1, result.size());

        Map<String, Object> personMap = result.get(0);
        assertEquals(4L, personMap.get("id"));
        assertNull(personMap.get("nombre"));
        assertEquals("test@test.com", personMap.get("correo"));
        assertNull(personMap.get("rol"));

        verify(personRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListarPersonas_VerifyMapStructure() {
        when(personRepository.findAll()).thenReturn(Arrays.asList(adminPerson));

        List<Map<String, Object>> result = personController.listarPersonas();

        Map<String, Object> personMap = result.get(0);

        assertTrue(personMap.containsKey("id"));
        assertTrue(personMap.containsKey("nombre"));
        assertTrue(personMap.containsKey("correo"));
        assertTrue(personMap.containsKey("rol"));

        assertEquals(4, personMap.size());

        verify(personRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListarPersonas_MultiplePersons() {
        List<Person> personas = Arrays.asList(clientePerson, adminPerson, meseroPerson);
        when(personRepository.findAll()).thenReturn(personas);

        List<Map<String, Object>> result = personController.listarPersonas();

        assertEquals(3, result.size());

        Map<String, Object> firstPerson = result.get(0);
        Map<String, Object> secondPerson = result.get(1);
        Map<String, Object> thirdPerson = result.get(2);

        assertEquals(3L, firstPerson.get("id"));
        assertEquals(1L, secondPerson.get("id"));
        assertEquals(2L, thirdPerson.get("id"));

        assertEquals("Cliente User", firstPerson.get("nombre"));
        assertEquals("Admin User", secondPerson.get("nombre"));
        assertEquals("Mesero User", thirdPerson.get("nombre"));

        verify(personRepository).findAll();
    }
}