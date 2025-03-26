package com.example.cabservice.service;

import com.example.cabservice.dto.AuthRequest;
import com.example.cabservice.dto.AuthResponse;
import com.example.cabservice.dto.RegisterRequest;
import com.example.cabservice.entity.Driver;
import com.example.cabservice.entity.Role;
import com.example.cabservice.entity.User;
import com.example.cabservice.repository.DriverRepository;
import com.example.cabservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public AuthResponse register(RegisterRequest request){
        if(request.getRole() == Role.DRIVER){
            Driver driver = Driver.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .available(true)
                    .role(Role.DRIVER)
                    .build();
            driverRepository.save(driver);
        } else {
            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
        }

        return new AuthResponse(jwtService.generateToken(request.getEmail(), request.getRole()));
    }

    public AuthResponse authenticate(AuthRequest authRequest){
        Optional<User> userOpt = userRepository.findByEmail(authRequest.getEmail());
        if(userOpt.isPresent() && passwordEncoder.matches(authRequest.getPassword(), userOpt.get().getPassword())){
            return new AuthResponse(jwtService.generateToken(userOpt.get().getEmail(), userOpt.get().getRole()));
        }

        Optional<Driver> driverOpt = driverRepository.findByEmail(authRequest.getEmail());
        if(driverOpt.isPresent() && passwordEncoder.matches(authRequest.getPassword(), driverOpt.get().getPassword())){
            return new AuthResponse(jwtService.generateToken(driverOpt.get().getEmail(), driverOpt.get().getRole()));
        }

        throw new RuntimeException("Invalid credentials");

    }
}
