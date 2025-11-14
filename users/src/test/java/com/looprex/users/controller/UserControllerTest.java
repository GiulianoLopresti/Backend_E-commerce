package com.looprex.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.looprex.users.dto.UserResponse;
import com.looprex.users.mapper.UserMapper;
import com.looprex.users.model.Role;
import com.looprex.users.model.User;
import com.looprex.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    private User testUser;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        Role role = new Role(2L, "CLIENT");
        
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setRut("12345678-9");
        testUser.setName("Juan");
        testUser.setLastname("Pérez");
        testUser.setPhone("912345678");
        testUser.setEmail("juan@test.com");
        testUser.setPassword("$2a$10$hashedPassword");
        testUser.setRole(role);
        testUser.setStatusId(1L);

        testUserResponse = UserResponse.builder()
                .userId(1L)
                .rut("12345678-9")
                .name("Juan")
                .lastName("Pérez")
                .phone("912345678")
                .email("juan@test.com")
                .roleId(2L)
                .roleName("CLIENT")
                .statusId(1L)
                .build();
    }

    @Test
    void login_DeberiaRetornar200CuandoCredencialesSonCorrectas() throws Exception {
        // Arrange
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "juan@test.com");
        credentials.put("password", "Test123!");

        when(userService.login("juan@test.com", "Test123!")).thenReturn(Optional.of(testUser));
        when(userMapper.toUserResponse(testUser)).thenReturn(testUserResponse);

        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("juan@test.com"));
    }

    @Test
    void getUserById_DeberiaRetornar200CuandoUsuarioExiste() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toUserResponse(testUser)).thenReturn(testUserResponse);

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1));
    }

    @Test
    void getUserById_DeberiaRetornar404CuandoUsuarioNoExiste() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void register_DeberiaRetornar201CuandoRegistroEsExitoso() throws Exception {
        // Arrange
        when(userService.register(any(User.class))).thenReturn(testUser);
        when(userMapper.toUserResponse(testUser)).thenReturn(testUserResponse);

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getAllUsers_DeberiaRetornar200ConListaDeUsuarios() throws Exception {
        
        List<User> users = Arrays.asList(testUser);
        
        when(userService.getAllUsers()).thenReturn(users);
        when(userMapper.toUserResponse(testUser)).thenReturn(testUserResponse);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void updatePersonalData_DeberiaRetornar200CuandoActualizacionEsExitosa() throws Exception {
        
        Map<String, String> data = new HashMap<>();
        data.put("rut", "98765432-1");
        data.put("name", "Pedro");
        data.put("lastName", "González");
        data.put("phone", "987654321");

        when(userService.updatePersonalData(eq(1L), any(), any(), any(), any()))
                .thenReturn(Optional.of(testUser));
        when(userMapper.toUserResponse(testUser)).thenReturn(testUserResponse);

        
        mockMvc.perform(put("/api/users/1/personal-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }   
}