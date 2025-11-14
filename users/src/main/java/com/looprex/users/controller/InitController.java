package com.looprex.users.controller;

import com.looprex.users.model.Role;
import com.looprex.users.model.User;
import com.looprex.users.repository.RoleRepository;
import com.looprex.users.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/init")
@Tag(name = "Inicialización", description = "Endpoints para cargar datos iniciales")
public class InitController {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InitController(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Cargar datos iniciales", description = "Inserta roles y usuario administrador por defecto")
    @PostMapping("/seed")
    public ResponseEntity<String> seedData() {
        StringBuilder mensaje = new StringBuilder();

        // Insertar roles si no existen
        if (roleRepository.count() == 0) {
            // Usar el constructor que acepta solo el nombre
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);

            Role clientRole = new Role();
            clientRole.setName("CLIENT");
            roleRepository.save(clientRole);

            mensaje.append("Roles creados: ADMIN, CLIENT. ");
        } else {
            mensaje.append("Roles ya existen. ");
        }

        // Insertar usuario admin si no existe
        if (!userRepository.existsByEmail("admin@looprex.cl")) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

            User admin = new User();
            admin.setRut("12345678-9");
            admin.setName("Admin");
            admin.setLastname("Sistema");
            admin.setPhone("912345678");
            admin.setEmail("admin@looprex.cl");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setRole(adminRole);
            admin.setStatusId(1L);
            userRepository.save(admin);

            mensaje.append("Usuario admin creado (email: admin@looprex.cl, password: Admin123!).");
        } else {
            mensaje.append("Usuario admin ya existe.");
        }

        return ResponseEntity.ok(mensaje.toString());
    }
}

//Cargar datos iniciales: POST http://localhost:8081/api/init/seed