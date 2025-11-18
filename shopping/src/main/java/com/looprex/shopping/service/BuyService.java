package com.looprex.shopping.service;

import com.looprex.shopping.client.AddressClient;
import com.looprex.shopping.client.StatusClient;
import com.looprex.shopping.client.UserClient;
import com.looprex.shopping.model.Buy;
import com.looprex.shopping.repository.BuyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BuyService {

    private final BuyRepository buyRepository;
    private final UserClient userClient;
    private final AddressClient addressClient;
    private final StatusClient statusClient;

    public BuyService(BuyRepository buyRepository,
                     UserClient userClient,
                     AddressClient addressClient,
                     StatusClient statusClient) {
        this.buyRepository = buyRepository;
        this.userClient = userClient;
        this.addressClient = addressClient;
        this.statusClient = statusClient;
    }

    public List<Buy> getAllBuys() {
        return buyRepository.findAllOrderByBuyDateDesc();
    }

    public Optional<Buy> getBuyById(Long id) {
        return buyRepository.findById(id);
    }

    public Optional<Buy> getBuyByOrderNumber(String orderNumber) {
        return buyRepository.findByOrderNumber(orderNumber);
    }

    public List<Buy> getBuysByUser(Long userId) {
        if (!userClient.userExists(userId)) {
            throw new IllegalArgumentException("El usuario con ID " + userId + " no existe");
        }
        return buyRepository.findByUserId(userId);
    }

    public List<Buy> getBuysByStatus(Long statusId) {
        if (!statusClient.statusExists(statusId)) {
            throw new IllegalArgumentException("El estado con ID " + statusId + " no existe");
        }
        return buyRepository.findByStatusId(statusId);
    }

    public Buy createBuy(Buy buy) {
        validateBuyFields(buy);
        validateBuyReferences(buy);

        // Establecer fecha si no viene
        if (buy.getBuyDate() == null) {
            buy.setBuyDate(System.currentTimeMillis());
        }

        // Guardar
        return buyRepository.save(buy);
    }

    private void validateBuyFields(Buy buy) {
        if (buy.getOrderNumber() == null || buy.getOrderNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de orden no puede estar vacío");
        }

        if (buy.getPaymentMethod() == null || buy.getPaymentMethod().trim().isEmpty()) {
            throw new IllegalArgumentException("El método de pago no puede estar vacío");
        }

        if (buy.getSubtotal() == null || buy.getSubtotal() < 0) {
            throw new IllegalArgumentException("El subtotal debe ser mayor o igual a 0");
        }

        if (buy.getIva() == null || buy.getIva() < 0) {
            throw new IllegalArgumentException("El IVA debe ser mayor o igual a 0");
        }

        if (buy.getShipping() == null || buy.getShipping() < 0) {
            throw new IllegalArgumentException("El costo de envío debe ser mayor o igual a 0");
        }

        if (buy.getTotal() == null || buy.getTotal() < 0) {
            throw new IllegalArgumentException("El total debe ser mayor o igual a 0");
        }

        if (buy.getUserId() == null) {
            throw new IllegalArgumentException("La compra debe estar asociada a un usuario");
        }

        if (buy.getAddressId() == null) {
            throw new IllegalArgumentException("La compra debe tener una dirección de envío");
        }

        if (buy.getStatusId() == null) {
            throw new IllegalArgumentException("La compra debe tener un estado");
        }
    }

    private void validateBuyReferences(Buy buy) {
        if (buyRepository.findByOrderNumber(buy.getOrderNumber()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una compra con el número de orden " + buy.getOrderNumber());
        }

        if (!userClient.userExists(buy.getUserId())) {
            throw new IllegalArgumentException("El usuario con ID " + buy.getUserId() + " no existe");
        }

        if (!addressClient.addressExists(buy.getAddressId())) {
            throw new IllegalArgumentException("La dirección con ID " + buy.getAddressId() + " no existe");
        }

        if (!statusClient.statusExists(buy.getStatusId())) {
            throw new IllegalArgumentException("El estado con ID " + buy.getStatusId() + " no existe");
        }
    }

    public Optional<Buy> updateBuy(Long id, Buy updatedBuy) {
        return buyRepository.findById(id).map(existingBuy -> {
            if (updatedBuy.getStatusId() != null) {
                if (!statusClient.statusExists(updatedBuy.getStatusId())) {
                    throw new IllegalArgumentException("El estado con ID " + updatedBuy.getStatusId() + " no existe");
                }
                existingBuy.setStatusId(updatedBuy.getStatusId());
            }

            if (updatedBuy.getPaymentMethod() != null && !updatedBuy.getPaymentMethod().trim().isEmpty()) {
                existingBuy.setPaymentMethod(updatedBuy.getPaymentMethod());
            }

            return buyRepository.save(existingBuy);
        });
    }

    public boolean deleteBuy(Long id) {
        if (buyRepository.existsById(id)) {
            buyRepository.deleteById(id);
            return true;
        }
        return false;
    }
}