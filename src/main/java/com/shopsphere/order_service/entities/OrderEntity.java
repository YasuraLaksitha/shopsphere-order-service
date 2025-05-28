package com.shopsphere.order_service.entities;

import com.shopsphere.order_service.utils.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "tbl_order")
@EntityListeners(AuditingEntityListener.class)
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private String code;

    private List<Long> orderItemIds;

    private OrderStatus orderStatus;

    private Double totalOrderPrice;
}
