package com.synchroteam.realmmodels;

//import io.realm.RealmObject;
//import io.realm.annotations.PrimaryKey;
//import io.realm.annotations.Required;

/**
 * Created by Trident on 10/31/2017.
 */

public class LocationCoordinates {

    private double latitude;

    private double longitude;

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
}
