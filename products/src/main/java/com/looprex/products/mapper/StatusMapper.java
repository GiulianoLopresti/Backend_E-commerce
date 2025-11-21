package com.looprex.products.mapper;

import com.looprex.products.dto.StatusResponse;
import com.looprex.products.model.Status;
import org.springframework.stereotype.Component;

@Component
public class StatusMapper {

    /**
     * Convierte una entidad Status a StatusResponse DTO
     */
    public StatusResponse toStatusResponse(Status status) {
        if (status == null) {
            return null;
        }

        return new StatusResponse(
            status.getStatusId(),
            status.getName()
        );
    }
}