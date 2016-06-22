package com.thecyclingapp.emiliyan.thecyclingapp.extras;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by Emiliyan on 4/29/2016.
 */
public abstract class DeviceLocationListener implements LocationListener{
    @Override
    public abstract void onLocationChanged(Location location);

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
