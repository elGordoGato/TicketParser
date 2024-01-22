package org.elgordogato;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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
