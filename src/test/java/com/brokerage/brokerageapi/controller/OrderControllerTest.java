package com.brokerage.brokerageapi.controller;

import com.brokerage.brokerageapi.config.JwtAuthFilter;
import com.brokerage.brokerageapi.config.SecurityConfig;
import com.brokerage.brokerageapi.model.Asset;
import com.brokerage.brokerageapi.model.Customer;
import com.brokerage.brokerageapi.model.Order;
import com.brokerage.brokerageapi.model.OrderSide;
import com.brokerage.brokerageapi.repository.AssetRepository;
import com.brokerage.brokerageapi.repository.CustomerRepository;
import com.brokerage.brokerageapi.request.OrderRequest;
import com.brokerage.brokerageapi.service.CustomerService;
import com.brokerage.brokerageapi.service.JwtTokenService;
import com.brokerage.brokerageapi.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @MockitoBean
    private CustomerService customerService;

    @Mock
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private AssetRepository assetRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user")
    void createOrderWhenCustomerIdMatchesShouldReturnOk() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setAssetName("TRY");
        request.setSize(10L);

        Order mockOrder = Order.builder()
                .id(1L)
                .customer(Customer.builder()
                        .id(1L)
                        .username("user")
                        .build())
                .orderSide(OrderSide.BUY)
                .size(10L)
                .price(10L)
                .asset(Asset.builder()
                        .assetName("TRY")
                        .build())
                .build();


        when(orderService.createOrder(request)).thenReturn(mockOrder);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.side").value("BUY"))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.customer.username").value("user"));
    }

    @Test
    @WithMockUser(username = "user")
    void createOrderWhenCustomerIdDoesNotMatchOrNotAdminShouldReturnForbidden() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setAssetName("TRY");
        request.setSize(10L);

        when(orderService.createOrder(request)).thenThrow(new AccessDeniedException("You are not allowed to create order for this customer."));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createOrderWhenThereIsNoUserShouldReturnForbidden() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(Long.getLong("1"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createOrderAsAdminShouldReturnOk() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setAssetName("TRY");
        request.setSize(10L);

        Order mockOrder = Order.builder()
                .id(1L)
                .customer(Customer.builder()
                        .id(2L)
                        .username("user")
                        .build())
                .orderSide(OrderSide.BUY)
                .size(10L)
                .price(10L)
                .asset(Asset.builder()
                        .assetName("TRY")
                        .build())
                .build();

        Mockito.when(orderService.createOrder(request)).thenReturn(mockOrder);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.side").value("BUY"))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.customer.username").value("user"));
    }

    @Test
    @WithMockUser(username = "user")
    void testGetOrders() throws Exception {
        Order mockOrder = Order.builder()
                .id(1L)
                .customer(Customer.builder()
                        .id(1L)
                        .username("user")
                        .build())
                .orderSide(OrderSide.BUY)
                .size(10L)
                .price(10L)
                .asset(Asset.builder()
                        .assetName("TRY")
                        .build())
                .build();

        when(orderService.listOrders(eq(1L), eq(false), eq("user"))).thenReturn(List.of(mockOrder));

        mockMvc.perform(get("/orders").param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].side").value("BUY"));
    }

    @Test
    @WithMockUser(username = "user")
    void testCancelOrder() throws Exception {
        doNothing().when(orderService).cancelOrder(1L, "user");

        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isOk());

        verify(orderService, times(1)).cancelOrder(1L, "user");
    }
}
