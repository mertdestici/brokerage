package com.brokerage.brokerageapi.service;

import com.brokerage.brokerageapi.model.Asset;
import com.brokerage.brokerageapi.model.Customer;
import com.brokerage.brokerageapi.model.NewCustomerRequest;
import com.brokerage.brokerageapi.repository.AssetRepository;
import com.brokerage.brokerageapi.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final AssetRepository assetRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsername(username)
                                              .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (customer.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new User(customer.getUsername(), customer.getPassword(), authorities);
    }

    public Customer saveCustomer(NewCustomerRequest customerRequest) {
        Optional<Customer> byUsername = customerRepository.findByUsername(customerRequest.getUsername());
        if (byUsername.isPresent()) {
            throw new RuntimeException("Customer found");
        }
        Customer customer = Customer.builder()
                .username(customerRequest.getUsername())
                .password(new BCryptPasswordEncoder().encode(customerRequest.getPassword()))
                .build();
        customerRepository.save(customer);
        Asset asset = Asset.builder()
                .assetName("TRY")
                .size(customerRequest.getAssetSize())
                .usableSize(customerRequest.getAssetSize())
                .customer(customer)
                .build();
        assetRepository.save(asset);
        return customer;
    }
}
