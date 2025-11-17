package com.looprex.geography.repository;

import com.looprex.geography.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // Obtener todas las direcciones de un usuario
    List<Address> findByUserId(Long userId);

    // Obtener la primera dirección de un usuario (la más usada en tu app)
    Optional<Address> findFirstByUserId(Long userId);

    // Obtener todas las direcciones de una comuna
    List<Address> findByComuna_ComunaId(Long comunaId);

    // Verificar si un usuario tiene direcciones
    boolean existsByUserId(Long userId);
}