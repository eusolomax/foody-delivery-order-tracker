package com.foodydelivery.order_tracker.auth.service;

import java.util.Locale;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foodydelivery.order_tracker.auth.dto.LoginRequest;
import com.foodydelivery.order_tracker.auth.dto.LoginResponse;
import com.foodydelivery.order_tracker.auth.dto.RegisterRequest;
import com.foodydelivery.order_tracker.auth.exception.DuplicateEmailException;
import com.foodydelivery.order_tracker.user.entity.User;
import com.foodydelivery.order_tracker.user.mapper.UserMapper;
import com.foodydelivery.order_tracker.user.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        User user = userMapper.toEntity(request);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(request.email()))
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            return new LoginResponse(
                    jwtService.generateToken(user),
                    jwtService.getExpirationInMillis()
            );
        }

        throw new BadCredentialsException("Invalid credentials");
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
