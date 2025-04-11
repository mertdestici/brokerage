package com.brokerage.brokerageapi.controller;

import com.brokerage.brokerageapi.model.Customer;
import com.brokerage.brokerageapi.model.Order;
import com.brokerage.brokerageapi.model.OrderSide;
import com.brokerage.brokerageapi.model.OrderStatus;
import com.brokerage.brokerageapi.repository.CustomerRepository;
import com.brokerage.brokerageapi.repository.OrderRepository;
import com.brokerage.brokerageapi.service.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private OrderRepository orderRepo;
    @Autowired
    private JwtTokenService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;

    @BeforeEach
    void setup() {
        customerRepo.deleteAll();
        orderRepo.deleteAll();

        Customer customer = new Customer();
        customer.setId("cust1");
        customer.setUsername("user1");
        customer.setPassword(passwordEncoder.encode("pass"));
        customer.setAdmin(false);
        customerRepo.save(customer);

        Order order = new Order();
        order.setCustomerId("cust1");
        order.setAssetName("AAPL");
        order.setOrderSide(OrderSide.BUY);
        order.setSize(10);
        order.setPrice(5.0);
        order.setStatus(OrderStatus.PENDING);
        orderRepo.save(order);

        userToken = jwtService.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername("user1")
                        .password("pass")
                        .roles("USER")
                        .build()
                                            );
    }

    @Test
    void getOrders_AsAuthorizedUser_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/orders?customerId=cust1")
                                .header("Authorization", "Bearer " + userToken))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].assetName", is("AAPL")));
    }
}