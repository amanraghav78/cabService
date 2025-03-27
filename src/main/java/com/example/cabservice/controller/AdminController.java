package com.example.cabservice.controller;

import com.example.cabservice.entity.Booking;
import com.example.cabservice.entity.Driver;
import com.example.cabservice.entity.User;
import com.example.cabservice.repository.BookingRepository;
import com.example.cabservice.repository.DriverRepository;
import com.example.cabservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final BookingRepository bookingRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getAllDrivers(){
        return ResponseEntity.ok(driverRepository.findAll());
    }

    @GetMapping("bookings")
    public ResponseEntity<List<Booking>> getAllBookings(){
        return ResponseEntity.ok(bookingRepository.findAll());
    }


}
