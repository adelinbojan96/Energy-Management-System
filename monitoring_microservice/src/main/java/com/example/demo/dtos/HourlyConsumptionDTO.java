package com.example.demo.dtos;

public class HourlyConsumptionDTO {

    private int hour;
    private double consumption;

    public HourlyConsumptionDTO(int hour, double consumption) {
        this.hour = hour;
        this.consumption = consumption;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }
}