package com.shopsphere.order_service.repositories;

import com.shopsphere.order_service.entities.ShippingDetailsEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShippingDetailsCache extends CrudRepository<ShippingDetailsEntity, UUID> {

    Optional<ShippingDetailsEntity> findByOrderId(Long addressCacheId);
}
