package com.example.cabservice.controller;

import com.example.cabservice.dto.BookingRequest;
import com.example.cabservice.entity.Booking;
import com.example.cabservice.entity.User;
import com.example.cabservice.repository.BookingRepository;
import com.example.cabservice.repository.UserRepository;
import com.example.cabservice.service.BookingService;
import com.example.cabservice.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final JWTService jwtService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Booking> bookRide(@RequestBody BookingRequest request, HttpServletRequest httpRequest){
        String token = httpRequest.getHeader("Authorization").substring(7);
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        Booking booking = bookingService.bookCab(user.getId(), request);
        return ResponseEntity.ok(booking);
    }
}
