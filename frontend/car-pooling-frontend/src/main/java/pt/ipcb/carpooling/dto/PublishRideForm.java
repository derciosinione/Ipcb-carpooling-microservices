package pt.ipcb.carpooling.dto;

import lombok.Data;

@Data
public class PublishRideForm {
    private String origin;
    private String destination;
    private Double originLat;
    private Double originLon;
    private Double destinationLat;
    private Double destinationLon;
    private String date;
    private String time;
    private Integer seats;
    private String vehicleId;
    private String description;
}
