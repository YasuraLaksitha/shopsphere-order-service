package com.shopsphere.order_service.repositories;

import com.shopsphere.order_service.entities.RetryEventEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RetryEventRepository extends CrudRepository<RetryEventEntity, Long> {

    Optional<RetryEventEntity> findByOrderId(Long orderId);
}
