package com.example.nj_ba.termproject;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {
    public double latitude, longitude, altitude;

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude(); //위도
        longitude = location.getLongitude(); //경도
        altitude = location.getAltitude(); //해발고도
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
