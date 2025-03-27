package com.example.cabservice.service;

import com.example.cabservice.dto.BookingRequest;
import com.example.cabservice.entity.Booking;
import com.example.cabservice.entity.Driver;
import com.example.cabservice.repository.BookingRepository;
import com.example.cabservice.repository.DriverRepository;
import com.example.cabservice.util.DistanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final DriverRepository driverRepository;
    private BookingRepository bookingRepository;

    public Booking bookCab(Long userId, BookingRequest bookingRequest){
        List<Driver> availableDrivers = driverRepository.findAll().stream()
                .filter(Driver::isAvailable)
                .collect(Collectors.toList());

        if(availableDrivers.isEmpty()){
            throw new RuntimeException("No drivers available");
        }

        Driver nearestDriver = null;
        double minDistance = Double.MAX_VALUE;

        for (Driver driver : availableDrivers){
            double distance = DistanceUtil.calculate(
                    driver.getCurrentLatitude(), driver.getCurrentLongitude(),
                    bookingRequest.getPickupLat(), bookingRequest.getPickupLng()
            );

            if(distance < minDistance){
                minDistance = distance;
                nearestDriver = driver;
            }
        }

        if(nearestDriver == null){
            throw new RuntimeException("No nearby drivers found");
        }

        double routeDistance = DistanceUtil.calculate(
                bookingRequest.getPickupLat(), bookingRequest.getPickupLng(),
                bookingRequest.getDropLat(), bookingRequest.getDropLng()
                );

        nearestDriver.setAvailable(false);
        driverRepository.save(nearestDriver);

        Booking booking = Booking.builder()
                .driverId(nearestDriver.getId())
                .pickupLat(bookingRequest.getPickupLat())
                .pickupLng(bookingRequest.getPickupLng())
                .dropLat(bookingRequest.getDropLat())
                .dropLng(bookingRequest.getDropLng())
                .distance(routeDistance)
                .status("ASSIGNED")
                .build();

        return bookingRepository.save(booking);
    }

    public Booking completeRide(Long bookingId, Long driverId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if(!booking.getDriverId().equals(driverId)){
            throw new RuntimeException("Driver not authorised to complete this ride");
        }

        booking.setStatus("COMPLETED");
        bookingRepository.save(booking);

        Driver driver = driverRepository.findById(driverId).orElseThrow(()-> new RuntimeException("Driver not found"));
        driver.setAvailable(true);
        driverRepository.save(driver);

        return booking;
    }
}
