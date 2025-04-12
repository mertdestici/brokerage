package com.brokerage.brokerageapi.service;

import com.brokerage.brokerageapi.model.Asset;
import com.brokerage.brokerageapi.model.Customer;
import com.brokerage.brokerageapi.repository.AssetRepository;
import com.brokerage.brokerageapi.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private AssetService assetService;

    private Customer testCustomer;
    private Asset testAsset;

    @BeforeEach
    void setup() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setUsername("user1");

        testAsset = new Asset();
        testAsset.setCustomer(testCustomer);
        testAsset.setAssetName("TRY");
        testAsset.setUsableSize(1000L);
    }

    @Test
    void testGetAssetsForCustomer_whenAdminAccess() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(assetRepository.findByCustomer(testCustomer)).thenReturn(List.of(testAsset));

        List<Asset> result = assetService.getAssetsForCustomer(1L, "admin", true);

        assertEquals(1, result.size());
        assertEquals("TRY", result.get(0).getAssetName());
    }

    @Test
    void testGetAssetsForCustomer_whenUserIsOwner() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(assetRepository.findByCustomer(testCustomer)).thenReturn(List.of(testAsset));

        List<Asset> result = assetService.getAssetsForCustomer(1L, "user1", false);

        assertEquals(1, result.size());
        assertEquals("TRY", result.get(0).getAssetName());
    }

    @Test
    void testGetAssetsForCustomer_whenUnauthorizedUser() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        assertThrows(RuntimeException.class, () ->
                assetService.getAssetsForCustomer(1L, "otherUser", false)
        );
    }

}

