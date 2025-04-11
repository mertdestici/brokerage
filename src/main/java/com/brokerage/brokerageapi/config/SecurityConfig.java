package com.brokerage.brokerageapi.config;

import com.brokerage.brokerageapi.model.Asset;
import com.brokerage.brokerageapi.model.Customer;
import com.brokerage.brokerageapi.repository.AssetRepository;
import com.brokerage.brokerageapi.repository.CustomerRepository;
import com.brokerage.brokerageapi.service.CustomerDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomerDetailsServiceImpl customerDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customerDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    CommandLineRunner initAdmin(CustomerRepository customerRepo,
                                AssetRepository assetRepo,
                                PasswordEncoder encoder) {
        return args -> {
            if (customerRepo.findByUsername("admin").isEmpty()) {
                Customer admin = Customer.builder()
                        .isAdmin(true)
                        .username("admin")
                        .password(encoder.encode("admin123"))
                                         .build();
                customerRepo.save(admin);

                Asset asset = Asset.builder()
                        .assetName("TRY")
                        .size(100000L)
                        .usableSize(100000L)
                        .customer(admin)
                        .build();
                assetRepo.save(asset);
            }
        };
    }
}
