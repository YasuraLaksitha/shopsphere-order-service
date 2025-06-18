package com.shopsphere.order_service.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.order_service.dto.DestinationAddressDTO;
import com.shopsphere.order_service.dto.ShippingRequestDTO;
import com.shopsphere.order_service.entities.DestinationAddressEntity;
import com.shopsphere.order_service.entities.ShippingDetailsEntity;
import com.shopsphere.order_service.exceptions.ResourceNotFoundException;
import com.shopsphere.order_service.repositories.DestinationAddressCache;
import com.shopsphere.order_service.repositories.ShippingDetailsCache;
import com.shopsphere.order_service.services.client.ICacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ICacheServiceImpl implements ICacheService {

    private final DestinationAddressCache destinationAddressCache;

    private final ShippingDetailsCache shippingDetailsCache;

    private final ObjectMapper objectMapper;

    @Override
    public void saveIntoCache(ShippingRequestDTO shippingRequestDTO, Long orderId, String userId) {
        final ShippingDetailsEntity shippingDetailsEntity = initializeShippingDetailsEntity(shippingRequestDTO, orderId,userId);

        final DestinationAddressEntity destinationAddressEntity = objectMapper
                .convertValue(shippingRequestDTO.getDestinationAddress(), DestinationAddressEntity.class);
        destinationAddressEntity.setCacheId(UUID.randomUUID());
        destinationAddressEntity.setShippingDetailsCacheId(shippingDetailsEntity.getCacheId());
        final DestinationAddressEntity savedAddressEntity = destinationAddressCache.save(destinationAddressEntity);

        shippingDetailsEntity.setAddressCacheId(savedAddressEntity.getCacheId());
        shippingDetailsCache.save(shippingDetailsEntity);
    }

    @Override
    public ShippingRequestDTO retrieveByOrderId(Long orderId) {
        final ShippingDetailsEntity detailsEntity = shippingDetailsCache.findByOrderId(orderId).orElseThrow(
                () -> new ResourceNotFoundException("ShippingDetails", "orderId", orderId.toString())
        );

        final DestinationAddressEntity addressEntity = destinationAddressCache.findByShippingDetailsCacheId(detailsEntity.getCacheId()).orElseThrow(
                () -> new ResourceNotFoundException("Destination Address", "shippingDetailsCacheId", detailsEntity.getCacheId().toString())
        );

        final DestinationAddressDTO destinationAddressRequest = objectMapper.convertValue(addressEntity, DestinationAddressDTO.class);
        final ShippingRequestDTO shippingRequest = objectMapper.convertValue(detailsEntity, ShippingRequestDTO.class);
        shippingRequest.setDestinationAddress(destinationAddressRequest);

        return shippingRequest;
    }

    private ShippingDetailsEntity initializeShippingDetailsEntity(ShippingRequestDTO shippingRequestDTO, Long orderId,String userId) {
        final ShippingDetailsEntity shippingDetailsEntity = objectMapper.convertValue(shippingRequestDTO, ShippingDetailsEntity.class);
        shippingDetailsEntity.setCacheId(UUID.randomUUID());
        shippingDetailsEntity.setOrderId(orderId);

        return shippingDetailsCache.save(shippingDetailsEntity);
    }
}
