package com.brokerage.brokerageapi.controller;

import com.brokerage.brokerageapi.config.JwtAuthFilter;
import com.brokerage.brokerageapi.config.SecurityConfig;
import com.brokerage.brokerageapi.model.Customer;
import com.brokerage.brokerageapi.model.Order;
import com.brokerage.brokerageapi.model.OrderSide;
import com.brokerage.brokerageapi.model.OrderStatus;
import com.brokerage.brokerageapi.repository.AssetRepository;
import com.brokerage.brokerageapi.repository.CustomerRepository;
import com.brokerage.brokerageapi.request.OrderRequest;
import com.brokerage.brokerageapi.service.CustomerService;
import com.brokerage.brokerageapi.service.JwtTokenService;
import com.brokerage.brokerageapi.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtTokenService jwtTokenService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private AssetRepository assetRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Order sampleOrder;

    private String token;

    public String getToken(){
        return token;
    }

    @BeforeEach
    void setUp() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUsername("user1");

        sampleOrder = new Order();
        sampleOrder.setId(1L);
        sampleOrder.setCustomer(customer);
        sampleOrder.setOrderSide(OrderSide.BUY);
        sampleOrder.setPrice(100L);
        sampleOrder.setSize(10L);
        sampleOrder.setStatus(OrderStatus.PENDING);
        sampleOrder.setCreateDate(LocalDateTime.now());

        token = Jwts.builder()
                .setSubject(customer.getUsername())
                .claim("roles", "ADMIN")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3000))
                .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256))
                .compact();
    }

    @Test
    void testCreateOrder() throws Exception {
        OrderRequest request = new OrderRequest(1L, "TRY", OrderSide.BUY, 10L, 100L);

        when(orderService.createOrder(request)).thenReturn(sampleOrder);

        MvcResult result = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetOrders() throws Exception {
        when(orderService.listOrders(eq(1L), eq(false), eq("user1"))).thenReturn(List.of(sampleOrder));

        mockMvc.perform(get("/orders")
                        .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderSide").value("BUY"));
    }

    @Test
    @WithMockUser(username = "user1")
    void testCancelOrder() throws Exception {
        doNothing().when(orderService).cancelOrder(1L, "user1");

        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isOk());

        verify(orderService, times(1)).cancelOrder(1L, "user1");
    }
}
