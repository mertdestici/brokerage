package com.brokerage.brokerageapi.service;

import com.brokerage.brokerageapi.model.Asset;
import com.brokerage.brokerageapi.model.Customer;
import com.brokerage.brokerageapi.model.Order;
import com.brokerage.brokerageapi.model.OrderSide;
import com.brokerage.brokerageapi.model.OrderStatus;
import com.brokerage.brokerageapi.repository.AssetRepository;
import com.brokerage.brokerageapi.repository.OrderRepository;
import com.brokerage.brokerageapi.request.OrderRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final AssetService assetService;
    private final OrderRepository orderRepo;

    public Order createOrder(OrderRequest request) {
        Asset asset = assetService.validateAssetBalance(request);

        Order order = Order.builder()
                           .customer(Customer.builder()
                                             .id(request.getCustomerId())
                                             .build())
                           .asset(Asset.builder()
                                       .assetName(request.getAssetName())
                                       .build())
                           .orderSide(request.getOrderSide())
                           .createDate(LocalDateTime.now())
                           .status(OrderStatus.PENDING)
                           .price(request.getPrice())
                           .size(request.getSize())
                           .build();

        assetService.updateUsableAssetBalance(asset, request);
        return orderRepo.save(order);
    }

    public void cancelOrder(Long orderId, String requester) {
        Order order = orderRepo.findById(orderId).orElseThrow();
        if (!order.getCustomer().getId().equals(requester)) {
            throw new AccessDeniedException("Cannot cancel other customer's order");
        }
        if (order.getStatus() != OrderStatus.PENDING)
            throw new RuntimeException("Cannot cancel non-pending order");

        assetService.checkAssetAndUpdate(order);

        order.setStatus(OrderStatus.CANCELED);
        orderRepo.save(order);
    }

    public List<Order> listOrders(Long customerId, boolean isAdmin, String requester) {
        if (!isAdmin && !requester.equals(customerId)) {
            throw new AccessDeniedException("Unauthorized access to customer orders");
        }
        return orderRepo.findByCustomerId(customerId);
    }


}
