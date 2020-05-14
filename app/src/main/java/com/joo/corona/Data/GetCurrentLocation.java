package com.joo.corona.Data;

public class GetCurrentLocation {
    double latitude;
    double longitude;
    String t;

    public GetCurrentLocation(double latitude, double longitude, String t) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.t = t;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }
}
