package com.example.aviation;

public class AirHoursData {
    private String flightDate;
    private String regNo;
    private double airHours;
    private String status;

    public AirHoursData(String flightDate, String regNo, double airHours, String status) {
        this.flightDate = flightDate;
        this.regNo = regNo;
        this.airHours = airHours;
        this.status = status;
    }

    // Getters
    public String getFlightDate() {
        return flightDate;
    }

    public String getRegNo() {
        return regNo;
    }

    public double getAirHours() {
        return airHours;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setFlightDate(String flightDate) {
        this.flightDate = flightDate;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public void setAirHours(double airHours) {
        this.airHours = airHours;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AirHoursData{" +
                "flightDate='" + flightDate + '\'' +
                ", regNo='" + regNo + '\'' +
                ", airHours=" + airHours +
                ", status='" + status + '\'' +
                '}';
    }
}