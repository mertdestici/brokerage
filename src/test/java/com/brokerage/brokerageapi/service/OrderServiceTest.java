package com.brokerage.brokerageapi.service;

import com.brokerage.brokerageapi.model.Asset;
import com.brokerage.brokerageapi.model.Order;
import com.brokerage.brokerageapi.model.OrderSide;
import com.brokerage.brokerageapi.model.OrderStatus;
import com.brokerage.brokerageapi.repository.AssetRepository;
import com.brokerage.brokerageapi.repository.OrderRepository;
import com.brokerage.brokerageapi.request.OrderRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AssetRepository assetRepository;
    @Mock
    private AssetService assetService;
    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_withSufficientTRY_shouldSucceed() {
        OrderRequest request = new OrderRequest("cust1", "AAPL", OrderSide.BUY, 2, 50);

        Asset tryAsset = new Asset();
        tryAsset.setAssetName("TRY");
        tryAsset.setCustomerId("cust1");
        tryAsset.setUsableSize(200);

        when(assetService.validateAssetBalance(request)).thenReturn(tryAsset);
        //when(assetRepository.findByCustomerIdAndAssetName("cust1", "TRY")).thenReturn(Optional.of(tryAsset));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order createdOrder = orderService.createOrder(request);

        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
        verify(orderRepository).save(any());
    }
}

