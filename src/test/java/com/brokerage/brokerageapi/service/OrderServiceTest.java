package com.brokerage.brokerageapi.service;

import com.brokerage.brokerageapi.model.*;
import com.brokerage.brokerageapi.repository.*;
import com.brokerage.brokerageapi.request.OrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AssetRepository assetRepository;
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private OrderService orderService;

    private Customer testCustomer;
    private Asset tryAsset;


    @BeforeEach
    void setup() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setUsername("user1");

        tryAsset = new Asset();
        tryAsset.setCustomer(testCustomer);
        tryAsset.setAssetName("TRY");
        tryAsset.setSize(10000L);
        tryAsset.setUsableSize(10000L);
    }

    @Test
    void testCreateBuyOrderWithSufficientBalance() {
        OrderRequest request = new OrderRequest(1L, "TRY", OrderSide.BUY, 10L, 100L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(assetRepository.findByCustomerAndAssetName(eq(testCustomer), eq("TRY"))).thenReturn(Optional.of(tryAsset));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(testCustomer, result.getCustomer());
        verify(assetRepository).save(any());
        verify(orderRepository).save(any());
    }

    @Test
    void testCreateOrderWithInsufficientBalance() {
        tryAsset.setUsableSize(100L);
        OrderRequest request = new OrderRequest(1L, "TRY", OrderSide.BUY, 10L, 100L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(assetRepository.findByCustomerAndAssetName(eq(testCustomer), eq("TRY"))).thenReturn(Optional.of(tryAsset));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(request);
        });

        assertTrue(ex.getMessage().contains("Not enough asset balance to place the order."));
    }
}

