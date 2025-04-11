package com.brokerage.brokerageapi.service;

import com.brokerage.brokerageapi.model.Asset;
import com.brokerage.brokerageapi.repository.AssetRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;
    @InjectMocks
    private AssetService assetService;

    @Test
    void getAssets_shouldReturnAssetsForCustomer() {
        List<Asset> assets = List.of(new Asset());
        when(assetRepository.findByCustomerId(1L)).thenReturn(assets);

        List<Asset> result = assetService.getAssetsForCustomer(1L);
        assertEquals(1, result.size());
    }

}

