package com.brokerage.brokerageapi.controller;

import com.brokerage.brokerageapi.model.Order;
import com.brokerage.brokerageapi.model.OrderResponse;
import com.brokerage.brokerageapi.request.OrderRequest;
import com.brokerage.brokerageapi.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.ok(new OrderResponse(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@RequestParam Long customerId,
                                                         @RequestParam(required = false, defaultValue = "false") boolean isAdmin,
                                                         @RequestParam(required = false, defaultValue = "") String requester) {
        List<Order> orders = orderService.listOrders(customerId, isAdmin, requester);
        List<OrderResponse> response = orders.stream().map(OrderResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId,
                                            @RequestParam(required = false, defaultValue = "") String requester) {
        orderService.cancelOrder(orderId, requester);
        return ResponseEntity.ok().build();
    }
}

