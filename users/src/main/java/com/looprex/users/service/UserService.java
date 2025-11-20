package com.looprex.users.service;

import com.looprex.users.model.User;
import com.looprex.users.model.Role;
import com.looprex.users.repository.UserRepository;
import com.looprex.users.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya está en uso");
        }
        
        Role role = roleRepository.findById(user.getRole().getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("El rol especificado no existe"));

        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public Optional<User> updatePersonalData(Long userId, String rut, String name, String lastName, String phone) {
        return userRepository.findById(userId).map(existingUser -> {
            
            if (rut != null && !existingUser.getRut().equals(rut) && userRepository.existsByRut(rut)) {
                throw new IllegalArgumentException("El RUT ya está en uso");
            }

            Optional.ofNullable(rut).ifPresent(existingUser::setRut);
            Optional.ofNullable(name).ifPresent(existingUser::setName);
            Optional.ofNullable(lastName).ifPresent(existingUser::setLastname);
            Optional.ofNullable(phone).ifPresent(existingUser::setPhone);

            return userRepository.save(existingUser);
        });
    }

    @Transactional
    public Optional<User> updateProfilePhoto(Long userId, String photoUri) {
        return userRepository.findById(userId).map(user -> {
            user.setProfilePhoto(photoUri);
            return userRepository.save(user);
        });
    }

    @Transactional
    public Optional<User> updatePassword(Long userId, String currentPassword, String newPassword) {
        return userRepository.findById(userId).map(user -> {
        
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new IllegalArgumentException("La contraseña actual es incorrecta");
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
            }
            
            user.setPassword(passwordEncoder.encode(newPassword));
            return userRepository.save(user);
        });
    }

    @Transactional
    public Optional<User> updateEmail(Long userId, String newEmail, String confirmPassword) {
        return userRepository.findById(userId).map(user -> {

            if (!passwordEncoder.matches(confirmPassword, user.getPassword())) {
                throw new IllegalArgumentException("La contraseña es incorrecta");
            }

            if (userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("El email ya está en uso");
            }

            user.setEmail(newEmail);
            return userRepository.save(user);
        });
    }
}