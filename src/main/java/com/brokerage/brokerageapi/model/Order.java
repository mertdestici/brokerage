package com.brokerage.brokerageapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @Enumerated(EnumType.STRING)
    private OrderSide orderSide;

    private Long size;
    private Long price;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDateTime createDate;
}
