package com.looprex.users.controller;

import com.looprex.users.dto.RoleResponse;
import com.looprex.users.mapper.RoleMapper;
import com.looprex.users.model.Role;
import com.looprex.users.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private RoleMapper roleMapper;

    private Role adminRole;
    private Role clientRole;
    private RoleResponse adminRoleResponse;
    private RoleResponse clientRoleResponse;

    @BeforeEach
    void setUp() {
        adminRole = new Role(1L, "ADMIN");
        clientRole = new Role(2L, "CLIENT");

        adminRoleResponse = RoleResponse.builder()
                .roleId(1L)
                .name("ADMIN")
                .build();

        clientRoleResponse = RoleResponse.builder()
                .roleId(2L)
                .name("CLIENT")
                .build();
    }

    @Test
    void getAllRoles_DeberiaRetornar200ConListaDeRoles() throws Exception {
        // Arrange
        List<Role> roles = Arrays.asList(adminRole, clientRole);

        when(roleService.getAllRoles()).thenReturn(roles);
        when(roleMapper.toRoleResponse(adminRole)).thenReturn(adminRoleResponse);
        when(roleMapper.toRoleResponse(clientRole)).thenReturn(clientRoleResponse);

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("ADMIN"))
                .andExpect(jsonPath("$.data[1].name").value("CLIENT"))
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    void getAllRoles_DeberiaRetornar204CuandoNoHayRoles() throws Exception {
    
        when(roleService.getAllRoles()).thenReturn(List.of());

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isNoContent()) 
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No se encontraron roles en el sistema"));
    }
}