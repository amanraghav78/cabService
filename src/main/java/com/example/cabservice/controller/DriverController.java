package com.example.cabservice.controller;

import com.example.cabservice.dto.CompleteRideRequest;
import com.example.cabservice.dto.LocationUpdateRequest;
import com.example.cabservice.entity.Booking;
import com.example.cabservice.entity.Driver;
import com.example.cabservice.repository.DriverRepository;
import com.example.cabservice.service.BookingService;
import com.example.cabservice.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverRepository driverRepository;
    private final JWTService jwtService;
    private final BookingService bookingService;

    @PutMapping("/location")
    public ResponseEntity<String> updateLocation(@RequestBody LocationUpdateRequest request, HttpServletRequest httpRequest){
        String token = httpRequest.getHeader("Authorization").substring(7);
        String email = jwtService.extractEmail(token);
        Driver driver = driverRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Driver not found"));

        driver.setCurrentLatitude(request.getLatitude());
        driver.setCurrentLongitude(request.getLongitude());
        driverRepository.save(driver);

        return ResponseEntity.ok("Location updated successfully");

    }

    @PostMapping("/complete")
    public ResponseEntity<Booking> completeRide(@RequestBody CompleteRideRequest request, HttpServletRequest httpRequest){
        String token = httpRequest.getHeader("Authorization").substring(7);
        String email = jwtService.extractEmail(token);
        Driver driver = driverRepository.findByEmail(email).orElseThrow(()->new RuntimeException("Driver not found"));

        Booking completedBooking = bookingService.completeRide(request.getBookingId(), driver.getId());
        return ResponseEntity.ok(completedBooking);
    }
}
