package com.shopsphere.order_service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Data
@AllArgsConstructor
@RedisHash(value = "shipping_retry", timeToLive = 1800L)
public class RetryEventEntity {

    @Id
    private UUID retryId;

    @Indexed
    private Long orderId;
}
