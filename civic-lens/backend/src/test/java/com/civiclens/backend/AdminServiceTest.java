package com.civiclens.backend;

import com.civiclens.backend.dto.UserDTO;
import com.civiclens.backend.entity.Admin;
import com.civiclens.backend.repository.AdminRepository;
import com.civiclens.backend.service.AdminService;
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
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    private UserDTO userDTO;
    private Admin admin;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setName("Test Admin");
        userDTO.setEmail("test@admin.com");
        userDTO.setPassword("password123");

        admin = new Admin();
        admin.setId(1L);
        admin.setName("Test Admin");
        admin.setEmail("test@admin.com");
        admin.setPassword("encodedPassword");
        admin.setRole("ADMIN");
    }

    @Test
    void registerAdmin_ShouldReturnSavedAdmin() {
        // Arrange
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        // Act
        Admin result = adminService.registerAdmin(userDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Test Admin", result.getName());
        assertEquals("test@admin.com", result.getEmail());
        assertEquals("ADMIN", result.getRole());
        verify(passwordEncoder).encode("password123");
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void findByEmail_ShouldReturnAdmin() {
        // Arrange
        when(adminRepository.findByEmail("test@admin.com")).thenReturn(Optional.of(admin));

        // Act
        Optional<Admin> result = adminService.findByEmail("test@admin.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Admin", result.get().getName());
        verify(adminRepository).findByEmail("test@admin.com");
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenAdminNotFound() {
        // Arrange
        when(adminRepository.findByEmail("nonexistent@admin.com")).thenReturn(Optional.empty());

        // Act
        Optional<Admin> result = adminService.findByEmail("nonexistent@admin.com");

        // Assert
        assertFalse(result.isPresent());
        verify(adminRepository).findByEmail("nonexistent@admin.com");
    }

    @Test
    void getAllAdmins_ShouldReturnListOfAdmins() {
        // Arrange
        List<Admin> adminList = Arrays.asList(admin);
        when(adminRepository.findAll()).thenReturn(adminList);

        // Act
        List<Admin> result = adminService.getAllAdmins();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Admin", result.get(0).getName());
        verify(adminRepository).findAll();
    }

    @Test
    void getAdminById_ShouldReturnAdmin() {
        // Arrange
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        // Act
        Optional<Admin> result = adminService.getAdminById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Admin", result.get().getName());
        verify(adminRepository).findById(1L);
    }

    @Test
    void getAdminById_ShouldReturnEmpty_WhenAdminNotFound() {
        // Arrange
        when(adminRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Admin> result = adminService.getAdminById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(adminRepository).findById(999L);
    }

    @Test
    void authenticateAdmin_ShouldReturnTrue_WhenCredentialsValid() {
        // Arrange
        when(adminRepository.findByEmail("test@admin.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        // Act
        boolean result = adminService.authenticateAdmin("test@admin.com", "password123");

        // Assert
        assertTrue(result);
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void authenticateAdmin_ShouldReturnFalse_WhenEmailNotFound() {
        // Arrange
        when(adminRepository.findByEmail("nonexistent@admin.com")).thenReturn(Optional.empty());

        // Act
        boolean result = adminService.authenticateAdmin("nonexistent@admin.com", "password123");

        // Assert
        assertFalse(result);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authenticateAdmin_ShouldReturnFalse_WhenPasswordInvalid() {
        // Arrange
        when(adminRepository.findByEmail("test@admin.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act
        boolean result = adminService.authenticateAdmin("test@admin.com", "wrongPassword");

        // Assert
        assertFalse(result);
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
    }
}
