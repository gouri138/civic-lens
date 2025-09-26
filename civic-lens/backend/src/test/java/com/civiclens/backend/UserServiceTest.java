package com.civiclens.backend;

import com.civiclens.backend.dto.UserDTO;
import com.civiclens.backend.entity.User;
import com.civiclens.backend.repository.UserRepository;
import com.civiclens.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
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

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;
    private User user;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setName("Test User");
        userDTO.setEmail("test@user.com");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@user.com");
        user.setPassword("encodedPassword");
        user.setRole("USER");
    }

    @Test
    void registerUser_ShouldReturnSavedUser() {
        // Arrange
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.registerUser(userDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@user.com", result.getEmail());
        assertEquals("USER", result.getRole());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail("test@user.com")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByEmail("test@user.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test User", result.get().getName());
        verify(userRepository).findByEmail("test@user.com");
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@user.com")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByEmail("nonexistent@user.com");

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail("nonexistent@user.com");
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        List<User> userList = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.getUserById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test User", result.get().getName());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findById(999L);
    }

    @Test
    void authenticateUser_ShouldReturnTrue_WhenCredentialsValid() {
        // Arrange
        when(userRepository.findByEmail("test@user.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        // Act
        boolean result = userService.authenticateUser("test@user.com", "password123");

        // Assert
        assertTrue(result);
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void authenticateUser_ShouldReturnFalse_WhenEmailNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@user.com")).thenReturn(Optional.empty());

        // Act
        boolean result = userService.authenticateUser("nonexistent@user.com", "password123");

        // Assert
        assertFalse(result);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authenticateUser_ShouldReturnFalse_WhenPasswordInvalid() {
        // Arrange
        when(userRepository.findByEmail("test@user.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act
        boolean result = userService.authenticateUser("test@user.com", "wrongPassword");

        // Assert
        assertFalse(result);
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
    }
}
