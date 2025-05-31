package com.resourcemap.service;

import com.resourcemap.model.User;
import com.resourcemap.model.UserRole;
import com.resourcemap.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void createUser_ShouldSaveUserWithEncodedPassword() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("plainPassword");
        user.setRole(UserRole.DONOR);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.createUser(user);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(user);
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        // Arrange
        User user = new User();
        user.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void createOAuthUser_ShouldCreateUserWithDonorRole() {
        // Arrange
        User savedUser = new User("oauth@example.com", "OAuth User", UserRole.DONOR);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.createOAuthUser("oauth@example.com", "OAuth User");

        // Assert
        assertNotNull(result);
        assertEquals(UserRole.DONOR, result.getRole());
        verify(userRepository).save(any(User.class));
    }
}