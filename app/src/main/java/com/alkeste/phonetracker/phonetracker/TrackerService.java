package com.alkeste.phonetracker.phonetracker;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * @author Kushagra Pandey
 * @class TrackerService is the background service which takes the position every few seconds
 */

public class TrackerService extends Service {

    private final String TAG = "TrackerService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 6000;
    private static final float LOCATION_DISTANCE = 1f;

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.i(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged: " + provider);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        init();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException e) {
            Log.i(TAG, "SecurityException", e);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "IllegalArgumentException: Provider does not exist " + e.getMessage());
        }

        Log.i(TAG, "Location from GPS: " + mLocationListeners[0].mLastLocation);
        Log.i(TAG, "Location from Network: " + mLocationListeners[1].mLastLocation);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception e) {
                    Log.i(TAG, "Exception while removing Location Listener ", e);
                }
            }
        }
    }

    private void init() {
        Log.i(TAG, "init");

        // Initializing Location Manager
        Log.i(TAG, "Initializing Location Manager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
