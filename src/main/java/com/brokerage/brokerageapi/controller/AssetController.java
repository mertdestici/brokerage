package com.brokerage.brokerageapi.controller;

import com.brokerage.brokerageapi.model.Asset;
import com.brokerage.brokerageapi.service.AssetService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/assets")
@AllArgsConstructor
public class AssetController {

    private AssetService assetService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Asset> getAssets(@RequestParam Long customerId, @AuthenticationPrincipal UserDetails user) {
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return assetService.getAssetsForCustomer(customerId, user.getUsername(), isAdmin);
    }
}
