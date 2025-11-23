package com.looprex.users.config;

import com.looprex.users.model.Role;
import com.looprex.users.model.User;
import com.looprex.users.repository.RoleRepository;
import com.looprex.users.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Verificar si ya existen roles
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, "ADMIN"));
            roleRepository.save(new Role(null, "CLIENT"));
        }

        // Verificar si ya existe el admin
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
        }
    }
}