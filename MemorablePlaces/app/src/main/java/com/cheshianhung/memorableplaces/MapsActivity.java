package com.cheshianhung.memorableplaces;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    LocationManager locationManager;
    LocationListener locationListener;
    private GoogleMap mMap;
    Integer placeNumber;
    boolean activated;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerLocationOnMap(lastKnownLocation, false, null);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        placeNumber = intent.getIntExtra("placeNumber", 0);

        activated = false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(placeNumber == MainActivity.aryList.size() - 1) {
            Toast.makeText(getApplicationContext(), "Long Press to Add New Location", Toast.LENGTH_SHORT).show();

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {

                    String address = latLngToAddress(latLng);

                    mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    MainActivity.locationList.add(latLng);
                    MainActivity.aryList.add(MainActivity.aryList.size() - 1, address);
                    MainActivity.arrayAdapter.notifyDataSetChanged();

                    Toast.makeText(getApplicationContext(),"Location added", Toast.LENGTH_SHORT).show();
                }
            });

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //Only track the user's location one time
                    if (!activated) {
                        activated = true;
                        centerLocationOnMap(location, true, "Your location");
                    }
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
            };

            if (Build.VERSION.SDK_INT < 23) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerLocationOnMap(lastKnownLocation, false, null);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    centerLocationOnMap(lastKnownLocation, false, null);
                }
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Long Press a New Location to Update", Toast.LENGTH_SHORT).show();
            mMap.addMarker(new MarkerOptions().position(MainActivity.locationList.get(placeNumber)).title(MainActivity.aryList.get(placeNumber)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.locationList.get(placeNumber), 13));

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    String address = latLngToAddress(latLng);

                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));


                    MainActivity.locationList.set(placeNumber,latLng);
                    MainActivity.aryList.set(placeNumber, address);
                    MainActivity.arrayAdapter.notifyDataSetChanged();

                    Toast.makeText(getApplicationContext(),"Location Updated", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void centerLocationOnMap(Location location, boolean hasMarker, String title) {
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 13));
        if (hasMarker)
            mMap.addMarker(new MarkerOptions().position(currentPosition).title(title));
    }

    private String latLngToAddress(LatLng latLng) {
        Location location = new Location("new");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);

        String address = "";

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(listAddress.size() > 0) {
                if(listAddress.get(0).getSubThoroughfare() != null) {
                    address += listAddress.get(0).getSubThoroughfare() + ", ";
                    if(listAddress.get(0).getThoroughfare() != null) {
                        address += listAddress.get(0).getThoroughfare() + ", ";
                    }
                    if(listAddress.get(0).getLocality() != null) {
                        address += listAddress.get(0).getLocality();
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        if(address.equals("")){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm MM-dd-yyyy");
            address = sdf.format(new Date());
        }
        return address;
    }
}
