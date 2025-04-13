package com.brokerage.brokerageapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCustomerRequest {
    private String username;
    private String password;
    private Long assetSize;
}
