package com.brokerage.brokerageapi.model;


import com.brokerage.brokerageapi.model.Order;
import com.brokerage.brokerageapi.model.OrderSide;
import com.brokerage.brokerageapi.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private OrderSide side;
    private Long price;
    private Long size;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.status = order.getStatus();
        this.side = order.getOrderSide();
        this.price = order.getPrice();
        this.size = order.getSize();
    }
}

