package com.looprex.geography.mapper;

import com.looprex.geography.dto.RegionResponse;
import com.looprex.geography.model.Region;
import org.springframework.stereotype.Component;

@Component
public class RegionMapper {

    public RegionResponse toRegionResponse(Region region) {
        if (region == null) {
            return null;
        }
        
        return RegionResponse.builder()
                .regionId(region.getRegionId())
                .name(region.getName())
                .build();
    }
}