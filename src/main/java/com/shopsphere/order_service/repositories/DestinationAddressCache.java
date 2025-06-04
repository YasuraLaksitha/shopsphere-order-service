package com.shopsphere.order_service.repositories;

import com.shopsphere.order_service.entities.DestinationAddressEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DestinationAddressCache extends CrudRepository<DestinationAddressEntity, UUID> {
    Optional<DestinationAddressEntity> findByOrderId(final Long orderId);
}
