package com.looprex.users.service;

import com.looprex.users.model.Role;
import com.looprex.users.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role adminRole;
    private Role clientRole;

    @BeforeEach
    void setUp() {
        adminRole = new Role(1L, "ADMIN");
        clientRole = new Role(2L, "CLIENT");
    }

    @Test
    void getAllRoles_DeberiaRetornarListaDeRoles() {
        
        List<Role> roles = Arrays.asList(adminRole, clientRole);
        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleService.getAllRoles();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ADMIN", result.get(0).getName());
        assertEquals("CLIENT", result.get(1).getName());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void getAllRoles_DeberiaRetornarListaVaciaCuandoNoHayRoles() {
        
        when(roleRepository.findAll()).thenReturn(List.of());

        List<Role> result = roleService.getAllRoles();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void getRoleById_DeberiaRetornarRolCuandoExiste() {
    
        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));

        Optional<Role> result = roleService.getRoleById(1L);

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    void getRoleById_DeberiaRetornarVacioCuandoNoExiste() {
        
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Role> result = roleService.getRoleById(999L);

        assertFalse(result.isPresent());
        verify(roleRepository, times(1)).findById(999L);
    }
}