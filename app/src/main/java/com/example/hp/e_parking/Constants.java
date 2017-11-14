package com.example.hp.e_parking;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by hp on 10/22/17.
 */

public class Constants {

    private Constants() {
    }

    private static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    static final float GEOFENCE_RADIUS_IN_METERS = 100; // 1 mile, 1.6 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    public static HashMap<String, LatLng> getAreaLandmarks(Context context) {
        HashMap<String, LatLng> AREA_LANDMARKS = new HashMap<>();

        LatLng savedArea = new LatLng(Double.valueOf(Vault.getSharedPreferencesString(context, "lat", "0.0"))
                , Double.valueOf(Vault.getSharedPreferencesString(context, "lng", "0.0")));
        if (savedArea.latitude > 0.0 && savedArea.longitude > 0.0) {
            AREA_LANDMARKS.put("FENCE", savedArea);
        }
        return AREA_LANDMARKS;
    }
}
