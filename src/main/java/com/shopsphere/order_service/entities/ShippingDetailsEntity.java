package com.shopsphere.order_service.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Data
@RedisHash(value = "shipping_details")
public class ShippingDetailsEntity  {

    @Id
    private UUID cacheId;

    @Indexed
    private Long orderId;

    private UUID trackingNumber;

    private UUID addressCacheId;

    private boolean isShippingMethodExpress;
}
