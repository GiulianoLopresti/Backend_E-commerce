package com.looprex.shopping.service;

import com.looprex.shopping.client.ProductClient;
import com.looprex.shopping.model.Detail;
import com.looprex.shopping.repository.BuyRepository;
import com.looprex.shopping.repository.DetailRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetailService {

    private final DetailRepository detailRepository;
    private final BuyRepository buyRepository;
    private final ProductClient productClient;

    public DetailService(DetailRepository detailRepository,
                        BuyRepository buyRepository,
                        ProductClient productClient) {
        this.detailRepository = detailRepository;
        this.buyRepository = buyRepository;
        this.productClient = productClient;
    }

    public List<Detail> getAllDetails() {
        return detailRepository.findAll();
    }

    public Optional<Detail> getDetailById(Long id) {
        return detailRepository.findById(id);
    }

    public List<Detail> getDetailsByBuy(Long buyId) {
        if (!buyRepository.existsById(buyId)) {
            throw new IllegalArgumentException("La compra con ID " + buyId + " no existe");
        }
        return detailRepository.findByBuyId(buyId);
    }

    public List<Detail> getDetailsByProduct(Long productId) {
        if (!productClient.productExists(productId)) {
            throw new IllegalArgumentException("El producto con ID " + productId + " no existe");
        }
        return detailRepository.findByProductId(productId);
    }

    public Detail createDetail(Detail detail) {
        // Validaciones básicas
        if (detail.getBuyId() == null) {
            throw new IllegalArgumentException("El detalle debe estar asociado a una compra");
        }

        if (detail.getProductId() == null) {
            throw new IllegalArgumentException("El detalle debe tener un producto");
        }

        if (detail.getQuantity() == null || detail.getQuantity() < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1");
        }

        if (detail.getUnitPrice() == null || detail.getUnitPrice() < 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor o igual a 0");
        }

        if (detail.getSubtotal() == null || detail.getSubtotal() < 0) {
            throw new IllegalArgumentException("El subtotal debe ser mayor o igual a 0");
        }

        // Validar que la compra existe
        if (!buyRepository.existsById(detail.getBuyId())) {
            throw new IllegalArgumentException("La compra con ID " + detail.getBuyId() + " no existe");
        }

        // Validar que el producto existe
        if (!productClient.productExists(detail.getProductId())) {
            throw new IllegalArgumentException("El producto con ID " + detail.getProductId() + " no existe");
        }

        // Guardar
        return detailRepository.save(detail);
    }

    public Optional<Detail> updateDetail(Long id, Detail updatedDetail) {
        return detailRepository.findById(id).map(existingDetail -> {
            updateQuantityIfPresent(updatedDetail, existingDetail);
            updateUnitPriceIfPresent(updatedDetail, existingDetail);
            updateSubtotalIfPresent(updatedDetail, existingDetail);
            updateProductIdIfPresent(updatedDetail, existingDetail);
            return detailRepository.save(existingDetail);
        });
    }

    private void updateQuantityIfPresent(Detail updatedDetail, Detail existingDetail) {
        if (updatedDetail.getQuantity() != null) {
            if (updatedDetail.getQuantity() < 1) {
                throw new IllegalArgumentException("La cantidad debe ser al menos 1");
            }
            existingDetail.setQuantity(updatedDetail.getQuantity());
        }
    }

    private void updateUnitPriceIfPresent(Detail updatedDetail, Detail existingDetail) {
        if (updatedDetail.getUnitPrice() != null) {
            if (updatedDetail.getUnitPrice() < 0) {
                throw new IllegalArgumentException("El precio unitario no puede ser negativo");
            }
            existingDetail.setUnitPrice(updatedDetail.getUnitPrice());
        }
    }

    private void updateSubtotalIfPresent(Detail updatedDetail, Detail existingDetail) {
        if (updatedDetail.getSubtotal() != null) {
            if (updatedDetail.getSubtotal() < 0) {
                throw new IllegalArgumentException("El subtotal no puede ser negativo");
            }
            existingDetail.setSubtotal(updatedDetail.getSubtotal());
        }
    }

    private void updateProductIdIfPresent(Detail updatedDetail, Detail existingDetail) {
        if (updatedDetail.getProductId() != null) {
            if (!productClient.productExists(updatedDetail.getProductId())) {
                throw new IllegalArgumentException("El producto con ID " + updatedDetail.getProductId() + " no existe");
            }
            existingDetail.setProductId(updatedDetail.getProductId());
        }
    }

    public boolean deleteDetail(Long id) {
        if (detailRepository.existsById(id)) {
            detailRepository.deleteById(id);
            return true;
        }
        return false;
    }
}