package com.shopsphere.order_service.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "tbl_order_item")
@EntityListeners(AuditingEntityListener.class)
public class OrderItemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    private Long orderId;

    private Long productName;

    private Integer quantity;

    private Double unitPrice;

    private Double totalPrice;
}
