package com.brokerage.brokerageapi.service;

import com.brokerage.brokerageapi.model.Asset;
import com.brokerage.brokerageapi.model.Customer;
import com.brokerage.brokerageapi.model.Order;
import com.brokerage.brokerageapi.model.OrderSide;
import com.brokerage.brokerageapi.model.OrderStatus;
import com.brokerage.brokerageapi.repository.AssetRepository;
import com.brokerage.brokerageapi.repository.CustomerRepository;
import com.brokerage.brokerageapi.repository.OrderRepository;
import com.brokerage.brokerageapi.request.OrderRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {

    private final AssetRepository assetRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public Order createOrder(OrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Asset asset = assetRepository.findByCustomerAndAssetName(customer, request.getAssetName())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        Long requiredAmount = request.getOrderSide() == OrderSide.BUY
                ? (int) (request.getPrice() * request.getSize())
                : request.getSize();

        if (asset.getUsableSize() < requiredAmount) {
            throw new RuntimeException("Not enough asset balance to place the order.");
        }

        asset.setUsableSize(asset.getUsableSize() - requiredAmount);
        assetRepository.save(asset);

        Order order = new Order();
        order.setCustomer(customer);
        order.setAsset(asset);
        order.setOrderSide(request.getOrderSide());
        order.setSize(request.getSize());
        order.setPrice(request.getPrice());
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now());

        return orderRepository.save(order);
    }

    public void cancelOrder(Long orderId, String requester) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Customer customer = order.getCustomer();

        if (!customer.getUsername().equals(requester)) {
            throw new AccessDeniedException("Unauthorized to cancel this order");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only pending orders can be cancelled.");
        }

        Long refundAmount = order.getOrderSide() == OrderSide.BUY
                ? order.getPrice() * order.getSize()
                : order.getSize();

        String refundAssetName = order.getOrderSide() == OrderSide.BUY ? "TRY" : order.getAsset().getAssetName();
        Asset refundAsset = assetRepository.findByCustomerAndAssetName(customer, refundAssetName)
                .orElseThrow(() -> new RuntimeException("Refund asset not found"));

        refundAsset.setUsableSize(refundAsset.getUsableSize() + refundAmount);
        assetRepository.save(refundAsset);

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    public List<Order> listOrders(Long customerId, boolean isAdmin, String requester) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (!isAdmin && !customer.getUsername().equals(requester)) {
            throw new AccessDeniedException("You can only access your own orders.");
        }

        return orderRepository.findByCustomer(customer);
    }


}
