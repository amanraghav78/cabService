package com.example.cabservice.controller;

import com.example.cabservice.entity.Booking;
import com.example.cabservice.entity.User;
import com.example.cabservice.repository.BookingRepository;
import com.example.cabservice.repository.UserRepository;
import com.example.cabservice.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final BookingRepository bookingRepository;
    private final JWTService jwtService;
    private final UserRepository userRepository;

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getUserBookings(HttpServletRequest httpRequest){
        String token = httpRequest.getHeader("Authorization".substring(7));
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings = bookingRepository.findByUserId(user.getId());
        return ResponseEntity.ok(bookings);
    }
}
