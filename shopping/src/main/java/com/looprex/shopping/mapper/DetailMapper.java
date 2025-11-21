package com.looprex.shopping.mapper;

import com.looprex.shopping.dto.DetailResponse;
import com.looprex.shopping.model.Detail;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DetailMapper {

    public DetailResponse toResponse(Detail detail) {
        if (detail == null) {
            return null;
        }

        DetailResponse response = new DetailResponse();
        response.setDetailId(detail.getDetailId());
        response.setBuyId(detail.getBuyId());
        response.setProductId(detail.getProductId());
        response.setQuantity(detail.getQuantity());
        response.setUnitPrice(detail.getUnitPrice());
        response.setSubtotal(detail.getSubtotal());
        return response;
    }

    public List<DetailResponse> toResponseList(List<Detail> details) {
        if (details == null) {
            return List.of();
        }
        return details.stream()
                .map(this::toResponse)
                .toList();
    }

    public Detail toEntity(DetailResponse response) {
        if (response == null) {
            return null;
        }

        Detail detail = new Detail();
        detail.setDetailId(response.getDetailId());
        detail.setBuyId(response.getBuyId());
        detail.setProductId(response.getProductId());
        detail.setQuantity(response.getQuantity());
        detail.setUnitPrice(response.getUnitPrice());
        detail.setSubtotal(response.getSubtotal());
        return detail;
    }
}