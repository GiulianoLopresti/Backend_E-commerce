package com.looprex.shopping.mapper;

import com.looprex.shopping.dto.BuyResponse;
import com.looprex.shopping.model.Buy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BuyMapper {

    public BuyResponse toResponse(Buy buy) {
        if (buy == null) {
            return null;
        }

        BuyResponse response = new BuyResponse();
        response.setBuyId(buy.getBuyId());
        response.setOrderNumber(buy.getOrderNumber());
        response.setBuyDate(buy.getBuyDate());
        response.setSubtotal(buy.getSubtotal());
        response.setIva(buy.getIva());
        response.setShipping(buy.getShipping());
        response.setTotal(buy.getTotal());
        response.setPaymentMethod(buy.getPaymentMethod());
        response.setStatusId(buy.getStatusId());
        response.setAddressId(buy.getAddressId());
        response.setUserId(buy.getUserId());
        return response;
    }

    public List<BuyResponse> toResponseList(List<Buy> buys) {
        if (buys == null) {
            return List.of();
        }
        return buys.stream()
                .map(this::toResponse)
                .toList();
    }

    public Buy toEntity(BuyResponse response) {
        if (response == null) {
            return null;
        }

        Buy buy = new Buy();
        buy.setBuyId(response.getBuyId());
        buy.setOrderNumber(response.getOrderNumber());
        buy.setBuyDate(response.getBuyDate());
        buy.setSubtotal(response.getSubtotal());
        buy.setIva(response.getIva());
        buy.setShipping(response.getShipping());
        buy.setTotal(response.getTotal());
        buy.setPaymentMethod(response.getPaymentMethod());
        buy.setStatusId(response.getStatusId());
        buy.setAddressId(response.getAddressId());
        buy.setUserId(response.getUserId());
        return buy;
    }
}