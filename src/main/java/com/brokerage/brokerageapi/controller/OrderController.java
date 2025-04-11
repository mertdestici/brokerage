package com.brokerage.brokerageapi.controller;

import com.brokerage.brokerageapi.model.Order;
import com.brokerage.brokerageapi.request.OrderRequest;
import com.brokerage.brokerageapi.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasRole('ADMIN') or #customerId == authentication.name")
    @GetMapping
    public List<Order> getOrders(@RequestParam Long customerId, @AuthenticationPrincipal UserDetails user) {
        boolean isAdmin = user.getAuthorities().stream()
                              .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return orderService.listOrders(customerId, isAdmin, user.getUsername());
    }

    @PreAuthorize("hasRole('ADMIN') or #request.customerId == authentication.name")
    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOwner(#id, authentication.name)")
    @DeleteMapping("/{id}")
    public void cancelOrder(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        orderService.cancelOrder(id, user.getUsername());
    }
}
