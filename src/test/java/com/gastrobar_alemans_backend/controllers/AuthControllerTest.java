package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.model.LoginModel;
import com.gastrobar_alemans_backend.model.Person;
import com.gastrobar_alemans_backend.repository.PersonRepository;
import com.gastrobar_alemans_backend.security.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private PersonRepository personRepo;

    @Mock
    private JWTUtil jwt;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private Person testPerson;
    private LoginModel validLogin;
    private LoginModel invalidLogin;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setCorreo("test@example.com");
        testPerson.setPass("encodedPassword");
        testPerson.setNombre("Test User");
        testPerson.setRol("admin");

        validLogin = new LoginModel();
        validLogin.setCorreo("test@example.com");
        validLogin.setPass("validPassword");

        invalidLogin = new LoginModel();
        invalidLogin.setCorreo("nonexistent@example.com");
        invalidLogin.setPass("wrongPassword");
    }

    @Test
    void testGenerarHash_Success() {
        // Arrange
        String rawPassword = "testPassword";
        String encodedPassword = "encodedTestPassword";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        String result = authController.generarHash(rawPassword);

        assertEquals(encodedPassword, result);
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void testRefreshToken_ValidToken() {
        // Arrange
        String refreshToken = "validRefreshToken";
        String email = "test@example.com";
        String newToken = "newAccessToken";

        when(jwt.validateTokenAndRetrieveSubject(refreshToken)).thenReturn(email);
        when(jwt.generateToken(email)).thenReturn(newToken);

        ResponseEntity<?> response = authController.refreshToken(refreshToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains(newToken));
        verify(jwt).validateTokenAndRetrieveSubject(refreshToken);
        verify(jwt).generateToken(email);
    }

    @Test
    void testRefreshToken_InvalidToken() {
        // Arrange
        String refreshToken = "invalidRefreshToken";
        when(jwt.validateTokenAndRetrieveSubject(refreshToken)).thenReturn(null);

        // Act
        ResponseEntity<?> response = authController.refreshToken(refreshToken);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Refresh token expirado.", response.getBody());
        verify(jwt).validateTokenAndRetrieveSubject(refreshToken);
        verify(jwt, never()).generateToken(anyString());
    }

    @Test
    void testLogin_Success() {
        String token = "accessToken";
        String refreshToken = "refreshToken";

        when(personRepo.findByCorreo(validLogin.getCorreo())).thenReturn(Optional.of(testPerson));
        when(passwordEncoder.matches(validLogin.getPass(), testPerson.getPass())).thenReturn(true);
        when(jwt.generateToken(testPerson.getCorreo())).thenReturn(token);
        when(jwt.generateRefreshToken(testPerson.getCorreo())).thenReturn(refreshToken);

        ResponseEntity<?> response = authController.login(validLogin);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        String responseBody = response.getBody().toString();
        assertTrue(responseBody.contains(token));
        assertTrue(responseBody.contains(refreshToken));
        assertTrue(responseBody.contains(testPerson.getNombre()));
        assertTrue(responseBody.contains(testPerson.getRol()));

        verify(personRepo).findByCorreo(validLogin.getCorreo());
        verify(passwordEncoder).matches(validLogin.getPass(), testPerson.getPass());
        verify(jwt).generateToken(testPerson.getCorreo());
        verify(jwt).generateRefreshToken(testPerson.getCorreo());
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        when(personRepo.findByCorreo(invalidLogin.getCorreo())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = authController.login(invalidLogin);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Correo no registrado.", response.getBody());
        verify(personRepo).findByCorreo(invalidLogin.getCorreo());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwt, never()).generateToken(anyString());
    }

    @Test
    void testLogin_WrongPassword() {
        // Arrange
        when(personRepo.findByCorreo(validLogin.getCorreo())).thenReturn(Optional.of(testPerson));
        when(passwordEncoder.matches(validLogin.getPass(), testPerson.getPass())).thenReturn(false);

        // Act
        ResponseEntity<?> response = authController.login(validLogin);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Contraseña incorrecta.", response.getBody());
        verify(personRepo).findByCorreo(validLogin.getCorreo());
        verify(passwordEncoder).matches(validLogin.getPass(), testPerson.getPass());
        verify(jwt, never()).generateToken(anyString());
    }

    @Test
    void testLogin_NullEmail() {
        LoginModel loginWithNullEmail = new LoginModel();
        loginWithNullEmail.setCorreo(null);
        loginWithNullEmail.setPass("password");
        assertDoesNotThrow(() -> {
            when(personRepo.findByCorreo(null)).thenReturn(Optional.empty());
            ResponseEntity<?> response = authController.login(loginWithNullEmail);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        });
    }

    @Test
    void testLogin_EmptyPassword() {
        // Arrange
        LoginModel loginWithEmptyPassword = new LoginModel();
        loginWithEmptyPassword.setCorreo("test@example.com");
        loginWithEmptyPassword.setPass("");

        when(personRepo.findByCorreo(loginWithEmptyPassword.getCorreo())).thenReturn(Optional.of(testPerson));
        when(passwordEncoder.matches("", testPerson.getPass())).thenReturn(false);

        // Act
        ResponseEntity<?> response = authController.login(loginWithEmptyPassword);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Contraseña incorrecta.", response.getBody());
    }

    @Test
    void testLogin_WithDifferentRoles() {
        // Arrange
        String[] roles = {"admin", "mesero", "cocinero", "user"};

        for (String role : roles) {
            testPerson.setRol(role);
            when(personRepo.findByCorreo(validLogin.getCorreo())).thenReturn(Optional.of(testPerson));
            when(passwordEncoder.matches(validLogin.getPass(), testPerson.getPass())).thenReturn(true);
            when(jwt.generateToken(testPerson.getCorreo())).thenReturn("token");
            when(jwt.generateRefreshToken(testPerson.getCorreo())).thenReturn("refresh");


            ResponseEntity<?> response = authController.login(validLogin);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().toString().contains(role));

            // Reset mocks para la siguiente iteración
            reset(personRepo, passwordEncoder, jwt);
        }
    }
}