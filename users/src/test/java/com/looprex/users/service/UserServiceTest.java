package com.looprex.users.service;

import com.looprex.users.model.Role;
import com.looprex.users.model.User;
import com.looprex.users.repository.RoleRepository;
import com.looprex.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setRoleId(2L);
        testRole.setName("CLIENT");

        testUser = new User();
        testUser.setRut("12345678-9");
        testUser.setName("Juan");
        testUser.setLastname("Pérez");
        testUser.setPhone("912345678");
        testUser.setEmail("juan@test.com");
        testUser.setPassword("Test123!");
        testUser.setRole(testRole);
        testUser.setStatusId(1L);
    }

    @Test
    void register_DeberiaCrearUsuarioExitosamente() {
        
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.existsByRut(testUser.getRut())).thenReturn(false);
        when(roleRepository.findById(2L)).thenReturn(Optional.of(testRole));
        when(passwordEncoder.encode(any())).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.register(testUser);

        assertNotNull(result);
        assertEquals("juan@test.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("Test123!");
    }

    @Test
    void register_DeberiaLanzarExcepcionCuandoEmailYaExiste() {
        
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.register(testUser)
        );
        
        assertEquals("El correo electrónico ya está en uso", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_DeberiaRetornarUsuarioCuandoCredencialesSonCorrectas() {
        
        String email = "juan@test.com";
        String password = "Test123!";
        testUser.setPassword("$2a$10$hashedPassword");
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(true);

        Optional<User> result = userService.login(email, password);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    @Test
    void login_DeberiaRetornarVacioCuandoCredencialesSonIncorrectas() {
        
        String email = "juan@test.com";
        String password = "WrongPassword";
        testUser.setPassword("$2a$10$hashedPassword");
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(false);

        Optional<User> result = userService.login(email, password);

        assertFalse(result.isPresent());
    }

    @Test
    void getUserById_DeberiaRetornarUsuarioCuandoExiste() {
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("juan@test.com", result.get().getEmail());
    }
    
    @Test
    void getAllUsers_DeberiaRetornarListaDeUsuarios() {

        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updatePersonalData_DeberiaActualizarDatosCorrectamente() {
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByRut("98765432-1")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Optional<User> result = userService.updatePersonalData(1L, "98765432-1", "Pedro", "González", "987654321");

        assertTrue(result.isPresent());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateProfilePhoto_DeberiaActualizarFotoCorrectamente() {
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Optional<User> result = userService.updateProfilePhoto(1L, "https://example.com/new-photo.jpg");

        assertTrue(result.isPresent());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updatePassword_DeberiaActualizarPasswordCorrectamente() {

        testUser.setPassword("$2a$10$oldHashedPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("OldPass123!", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("NewPass123!")).thenReturn("$2a$10$newHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Optional<User> result = userService.updatePassword(1L, "OldPass123!", "NewPass123!");

        assertTrue(result.isPresent());
        verify(passwordEncoder, times(1)).encode("NewPass123!");
    }

    @Test
    void updatePassword_DeberiaLanzarExcepcionCuandoPasswordActualEsIncorrecta() {
        
        testUser.setPassword("$2a$10$hashedPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPassword", testUser.getPassword())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, 
            () -> userService.updatePassword(1L, "WrongPassword", "NewPass123!"));
    }

    @Test
    void updateEmail_DeberiaActualizarEmailCorrectamente() {
    
        testUser.setPassword("$2a$10$hashedPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Test123!", testUser.getPassword())).thenReturn(true);
        when(userRepository.existsByEmail("newemail@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Optional<User> result = userService.updateEmail(1L, "newemail@test.com", "Test123!");

        assertTrue(result.isPresent());
        verify(userRepository, times(1)).save(any(User.class));
}
}