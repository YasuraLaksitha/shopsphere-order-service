package com.shopsphere.order_service.repositories;

import com.shopsphere.order_service.entities.OrderEntity;
import com.shopsphere.order_service.entities.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}
