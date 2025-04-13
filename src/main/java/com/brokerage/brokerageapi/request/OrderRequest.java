package com.brokerage.brokerageapi.request;

import com.brokerage.brokerageapi.model.OrderSide;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Long customerId;
    private String assetName;
    private OrderSide orderSide;
    private Long size;
    private Long price;
}
