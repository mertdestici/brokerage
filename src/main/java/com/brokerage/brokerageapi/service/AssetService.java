package com.brokerage.brokerageapi.service;

import com.brokerage.brokerageapi.model.Asset;
import com.brokerage.brokerageapi.model.Customer;
import com.brokerage.brokerageapi.model.Order;
import com.brokerage.brokerageapi.model.OrderSide;
import com.brokerage.brokerageapi.repository.AssetRepository;
import com.brokerage.brokerageapi.repository.CustomerRepository;
import com.brokerage.brokerageapi.request.OrderRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final CustomerRepository customerRepository;

    public List<Asset> getAssetsForCustomer(Long requestedId) {
        return assetRepository.findByCustomerId(requestedId);
    }

    public Asset validateAssetBalance(OrderRequest request) {
        String assetName = request.getOrderSide() == OrderSide.BUY ? "TRY" : request.getAssetName();
        Customer customer = customerRepository.findById(request.getCustomerId())
                                              .orElseThrow(() -> new RuntimeException("Customer not found"));

        Asset asset = assetRepository.findByCustomerAndAssetName(customer, assetName)
                                     .orElseThrow(() -> new RuntimeException("Asset not found"));

        Long required = request.getSize() * (request.getOrderSide() == OrderSide.BUY ? request.getPrice() : 1);
        if (asset.getUsableSize() < required)
            throw new RuntimeException("Not enough balance");

        return asset;
    }

    public void updateUsableAssetBalance(Asset asset, OrderRequest request) {
        Long decrease = request.getSize() * (request.getOrderSide() == OrderSide.BUY ? request.getPrice() : 1);
        asset.setUsableSize(asset.getUsableSize() - decrease);
        assetRepository.save(asset);
    }


    public void checkAssetAndUpdate(Order order) {
        Asset asset = assetRepository.findByCustomerAndAssetName(order.getCustomer(),
                                                             order.getOrderSide() == OrderSide.BUY ? "TRY" : order.getAsset().getAssetName()).orElseThrow();

        Long refund = order.getSize() * (order.getOrderSide() == OrderSide.BUY ? order.getPrice() : 1);
        asset.setUsableSize(asset.getUsableSize() + refund);
        assetRepository.save(asset);
    }
}
