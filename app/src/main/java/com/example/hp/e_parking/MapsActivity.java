package com.example.hp.e_parking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context context=this;
    Button btn1, btn2;
    String name, vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn1 = (Button) findViewById(R.id.button);
        btn2 = (Button) findViewById(R.id.btnslot);
        name = getIntent().getExtras().getString("name");
        vehicle = getIntent().getExtras().getString("vehicle");

    }

    public void btnClickmap(View view) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        Intent chooser = Intent.createChooser(i, "Launch maps");
        startActivity(i);
    }

    public void btnClickslot(View v) {

        Intent i = new Intent(this, ParkingActivity.class);
        i.putExtra("name", name);
        i.putExtra("vehicle", vehicle);
        startActivity(i);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please provide location permission!!", Toast.LENGTH_SHORT);
            return;
        }
        mMap.setMyLocationEnabled(true);


        LatLng savedArea = new LatLng(Double.valueOf(Vault.getSharedPreferencesString(context, "lat", "0.0"))
                , Double.valueOf(Vault.getSharedPreferencesString(context, "lng", "0.0")));

        if (savedArea != null) {
            if (savedArea.latitude > 0.0 && savedArea.longitude > 0.0) {
                mMap.addMarker(new MarkerOptions().position(savedArea).title("Current Fence"));
            } else {
                Toast.makeText(this, "No fence set!!", Toast.LENGTH_SHORT);
            }
        }


        if (mMap != null) {

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    mMap.addMarker(new MarkerOptions().position(latLng).title("New Fence!!"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    //Vault.putSharedPreferencesString(context, "lat", String.valueOf(latLng.latitude));
                    //Vault.putSharedPreferencesString(context, "lng", String.valueOf(latLng.longitude));

                    //Toast.makeText(MapsActivity.this, "Please remove fence and add again!!", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(context, Home.class);
                    //startActivity(intent);
                    //finish();
                }

            });
        } else {
            Toast.makeText(this, "Map is null!!", Toast.LENGTH_SHORT);
        }
    }
}
