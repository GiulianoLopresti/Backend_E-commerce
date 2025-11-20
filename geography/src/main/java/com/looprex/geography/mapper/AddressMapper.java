package com.looprex.geography.mapper;

import com.looprex.geography.dto.AddressResponse;
import com.looprex.geography.dto.ComunaResponse;
import com.looprex.geography.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    private final ComunaMapper comunaMapper;

    public AddressMapper(ComunaMapper comunaMapper) {
        this.comunaMapper = comunaMapper;
    }

    public AddressResponse toAddressResponse(Address address) {
        if (address == null) {
            return null;
        }

        ComunaResponse comunaResponse = comunaMapper.toComunaResponse(address.getComuna());

        return AddressResponse.builder()
                .addressId(address.getAddressId())
                .street(address.getStreet())
                .number(address.getNumber())
                .userId(address.getUserId())
                .comuna(comunaResponse) 
                .build();
    }
}