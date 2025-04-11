package com.brokerage.brokerageapi.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
    private boolean isAdmin;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Asset> assets;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders;
}
