package com.example.cabservice.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private double pickupLat;
    private double pickupLng;
    private double dropLat;
    private double dropLng;
}
