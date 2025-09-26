package com.civiclens.backend;

import com.civiclens.backend.controller.AdminController;
import com.civiclens.backend.dto.UserDTO;
import com.civiclens.backend.entity.Admin;
import com.civiclens.backend.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_ShouldReturnAdmin_WhenValidRequest() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Test Admin");
        userDTO.setEmail("test@admin.com");
        userDTO.setPassword("password123");

        Admin savedAdmin = new Admin();
        savedAdmin.setId(1L);
        savedAdmin.setName("Test Admin");
        savedAdmin.setEmail("test@admin.com");
        savedAdmin.setRole("ADMIN");

        when(adminService.registerAdmin(any(UserDTO.class))).thenReturn(savedAdmin);

        // Act & Assert
        mockMvc.perform(post("/api/admins/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Admin"))
                .andExpect(jsonPath("$.email").value("test@admin.com"));

        verify(adminService).registerAdmin(any(UserDTO.class));
    }

    @Test
    void login_ShouldReturnAdmin_WhenValidHardcodedCredentials() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("admin@civiclens.com");
        userDTO.setPassword("admin123");

        // Act & Assert
        mockMvc.perform(post("/api/admins/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Admin"))
                .andExpect(jsonPath("$.email").value("admin@civiclens.com"));

        // No service call for hardcoded login
        verify(adminService, never()).authenticateAdmin(anyString(), anyString());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("wrong@admin.com");
        userDTO.setPassword("wrong");

        // Act & Assert
        mockMvc.perform(post("/api/admins/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));

        verify(adminService, never()).authenticateAdmin(anyString(), anyString());
    }

    @Test
    void getAllAdmins_ShouldReturnListOfAdmins() throws Exception {
        // Arrange
        Admin admin1 = new Admin();
        admin1.setId(1L);
        admin1.setName("Admin 1");

        Admin admin2 = new Admin();
        admin2.setId(2L);
        admin2.setName("Admin 2");

        List<Admin> adminList = Arrays.asList(admin1, admin2);
        when(adminService.getAllAdmins()).thenReturn(adminList);

        // Act & Assert
        mockMvc.perform(get("/api/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Admin 1"))
                .andExpect(jsonPath("$[1].name").value("Admin 2"));

        verify(adminService).getAllAdmins();
    }

    @Test
    void getAdminById_ShouldReturnAdmin_WhenFound() throws Exception {
        // Arrange
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setName("Test Admin");

        when(adminService.getAdminById(1L)).thenReturn(Optional.of(admin));

        // Act & Assert
        mockMvc.perform(get("/api/admins/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Admin"));

        verify(adminService).getAdminById(1L);
    }

    @Test
    void getAdminById_ShouldReturnNotFound_WhenNotFound() throws Exception {
        // Arrange
        when(adminService.getAdminById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/admins/999"))
                .andExpect(status().isNotFound());

        verify(adminService).getAdminById(999L);
    }
}
