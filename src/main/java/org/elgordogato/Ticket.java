package org.elgordogato;

import lombok.Data;

@Data
public class Ticket {
    private String departureCity;
    private String departureTime;
    private int departureTimeZone;
    private String arrivalCity;
    private String arrivalTime;
    private int arrivalTimeZone;
    private String carrier;
    private int transfers;
    private double price;
}
