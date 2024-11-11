package com.synchroteam.beans;

/**
 * Created by Trident on 10/31/2017.
 */

public class LocationPoints {

    private double latitude;

    private double longitude;

    public LocationPoints(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
