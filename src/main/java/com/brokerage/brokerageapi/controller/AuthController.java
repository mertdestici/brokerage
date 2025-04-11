package com.brokerage.brokerageapi.controller;

import com.brokerage.brokerageapi.service.JwtTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenService jwtService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username, @RequestParam String password) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return Map.of("token", token);
    }
}
