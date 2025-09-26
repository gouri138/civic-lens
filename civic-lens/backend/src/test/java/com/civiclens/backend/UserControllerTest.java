package com.civiclens.backend;

import com.civiclens.backend.controller.UserController;
import com.civiclens.backend.dto.UserDTO;
import com.civiclens.backend.entity.User;
import com.civiclens.backend.service.UserService;
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

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_ShouldReturnUser_WhenValidRequest() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Test User");
        userDTO.setEmail("test@user.com");
        userDTO.setPassword("password123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Test User");
        savedUser.setEmail("test@user.com");
        savedUser.setRole("USER");

        when(userService.registerUser(any(UserDTO.class))).thenReturn(savedUser);

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@user.com"));

        verify(userService).registerUser(any(UserDTO.class));
    }

    @Test
    void login_ShouldReturnUser_WhenValidCredentials() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@user.com");
        userDTO.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@user.com");
        user.setRole("USER");

        when(userService.authenticateUser("test@user.com", "password123")).thenReturn(true);
        when(userService.findByEmail("test@user.com")).thenReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@user.com"));

        verify(userService).authenticateUser("test@user.com", "password123");
        verify(userService).findByEmail("test@user.com");
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@user.com");
        userDTO.setPassword("wrong");

        when(userService.authenticateUser("test@user.com", "wrong")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));

        verify(userService).authenticateUser("test@user.com", "wrong");
        verify(userService, never()).findByEmail(anyString());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setName("User 1");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User 2");

        List<User> userList = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(userList);

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[1].name").value("User 2"));

        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUser_WhenFound() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"));

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserById_ShouldReturnNotFound_WhenNotFound() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(999L);
    }
}
