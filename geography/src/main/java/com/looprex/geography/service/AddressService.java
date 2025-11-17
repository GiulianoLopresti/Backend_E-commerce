package com.looprex.geography.service;

import com.looprex.geography.client.UserClient;
import com.looprex.geography.model.Address;
import com.looprex.geography.repository.AddressRepository;
import com.looprex.geography.repository.ComunaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final ComunaRepository comunaRepository;
    private final UserClient userClient;

    public AddressService(AddressRepository addressRepository, 
                         ComunaRepository comunaRepository,
                         UserClient userClient) {
        this.addressRepository = addressRepository;
        this.comunaRepository = comunaRepository;
        this.userClient = userClient;
    }

    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    public Optional<Address> getAddressById(Long id) {
        return addressRepository.findById(id);
    }

    public List<Address> getAddressesByUserId(Long userId) {
        if (!userClient.userExists(userId)) {
            throw new IllegalArgumentException("El usuario con ID " + userId + " no existe");
        }
        return addressRepository.findByUserId(userId);
    }

     public Address createAddress(Address address) {
        // Validaciones básicas
        if (address.getStreet() == null || address.getStreet().trim().isEmpty()) {
            throw new IllegalArgumentException("La calle no puede estar vacía");
        }
        
        if (address.getNumber() == null || address.getNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("El número no puede estar vacío");
        }
        
        if (address.getUserId() == null) {
            throw new IllegalArgumentException("La dirección debe estar asociada a un usuario");
        }
        
        if (address.getComuna() == null || address.getComuna().getComunaId() == null) {
            throw new IllegalArgumentException("La dirección debe tener una comuna válida");
        }
        
        // Cargar la comuna completa desde la BD
        var comuna = comunaRepository.findById(address.getComuna().getComunaId())
                .orElseThrow(() -> new IllegalArgumentException("La comuna con ID " + address.getComuna().getComunaId() + " no existe"));
        
        address.setComuna(comuna);
        
        // Validar que el usuario existe (ANTES de guardar)
        if (!userClient.userExists(address.getUserId())) {
            throw new IllegalArgumentException("El usuario con ID " + address.getUserId() + " no existe");
        }
        
        // Guardar
        Address saved = addressRepository.save(address);
        
        return addressRepository.findById(saved.getAddressId()).orElse(saved);
    }

    public Optional<Address> updateAddress(Long id, Address updatedAddress) {
        return addressRepository.findById(id).map(existingAddress -> {
            updateStreetIfProvided(updatedAddress, existingAddress);
            updateNumberIfProvided(updatedAddress, existingAddress);
            updateComunaIfProvided(updatedAddress, existingAddress);
            updateUserIdIfProvided(updatedAddress, existingAddress);
            
            Address saved = addressRepository.save(existingAddress);
            
            // Recargar para obtener todas las relaciones
            return addressRepository.findById(saved.getAddressId()).orElse(saved);
        });
    }

    private void updateStreetIfProvided(Address updatedAddress, Address existingAddress) {
        if (updatedAddress.getStreet() != null && !updatedAddress.getStreet().trim().isEmpty()) {
            existingAddress.setStreet(updatedAddress.getStreet());
        }
    }

    private void updateNumberIfProvided(Address updatedAddress, Address existingAddress) {
        if (updatedAddress.getNumber() != null && !updatedAddress.getNumber().trim().isEmpty()) {
            existingAddress.setNumber(updatedAddress.getNumber());
        }
    }

    private void updateComunaIfProvided(Address updatedAddress, Address existingAddress) {
        if (updatedAddress.getComuna() != null) {
            if (!comunaRepository.existsById(updatedAddress.getComuna().getComunaId())) {
                throw new IllegalArgumentException("La comuna con ID " + updatedAddress.getComuna().getComunaId() + " no existe");
            }
            existingAddress.setComuna(updatedAddress.getComuna());
        }
    }

    private void updateUserIdIfProvided(Address updatedAddress, Address existingAddress) {
        if (updatedAddress.getUserId() != null) {
            if (!userClient.userExists(updatedAddress.getUserId())) {
                throw new IllegalArgumentException("El usuario con ID " + updatedAddress.getUserId() + " no existe");
            }
            existingAddress.setUserId(updatedAddress.getUserId());
        }
    }

    public boolean deleteAddress(Long id) {
        if (addressRepository.existsById(id)) {
            addressRepository.deleteById(id);
            return true;
        }
        return false;
    }
}