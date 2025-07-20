package com.mahjong.mahjongserver.api.controller;

import com.mahjong.mahjongserver.auth.AuthRequest;
import com.mahjong.mahjongserver.auth.JwtUtil;
import com.mahjong.mahjongserver.auth.User;
import com.mahjong.mahjongserver.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            return jwtUtil.generateToken(request.getUsername());
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid credentials!");
        }
    }

    @PostMapping("/register")
    public String register(@RequestBody AuthRequest request) {
        if (userRepository.findById(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username taken!");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setHashedPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(newUser);

        return jwtUtil.generateToken(newUser.getUsername());
    }
}