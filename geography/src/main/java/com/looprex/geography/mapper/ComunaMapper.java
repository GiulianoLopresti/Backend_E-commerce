package com.looprex.geography.mapper;

import com.looprex.geography.dto.ComunaResponse;
import com.looprex.geography.dto.RegionResponse;
import com.looprex.geography.model.Comuna;
import org.springframework.stereotype.Component;

@Component
public class ComunaMapper {

    private final RegionMapper regionMapper;

    public ComunaMapper(RegionMapper regionMapper) {
        this.regionMapper = regionMapper;
    }

    public ComunaResponse toComunaResponse(Comuna comuna) {
        if (comuna == null) {
            return null;
        }

        RegionResponse regionResponse = regionMapper.toRegionResponse(comuna.getRegion());

        return ComunaResponse.builder()
                .comunaId(comuna.getComunaId())
                .name(comuna.getName())
                .region(regionResponse) 
                .build();
    }
}