package com.shopsphere.order_service.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Data
@RedisHash(value = "destination_address", timeToLive = 900L)
public class DestinationAddressEntity {

    @Id
    private UUID cacheId;

    @Indexed
    private UUID shippingDetailsCacheId;

    private String number;

    private String street;

    private String city;

    private String state;

    private String postalCode;

    private String country;
}
