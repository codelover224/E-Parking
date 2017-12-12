package com.example.hp.e_parking.models;

/**
 * Created by hubbelsoftware on 11/14/17.
 */


public class Parking {

    public int position;
    public String message;
    public boolean isParked;
    public String email;
    public long startTime;
    public String name;

    public Parking() {
    }

    public Parking(int position, String message, boolean isParked, String email, long startTime, String name) {
        this.position = position;
        this.message = message;
        this.isParked = isParked;
        this.email = email;
        this.startTime = startTime;
        this.name = name;
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isParked() {
        return isParked;
    }

    public void setParked(boolean parked) {
        isParked = parked;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTime() {

        return startTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
