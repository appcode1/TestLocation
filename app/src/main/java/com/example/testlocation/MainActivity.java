package com.example.testlocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSION_LOCATION = 100;
    private Button startLocationUpdate, stopLocationUpdate;
    private TextView positionTextView;
    private LocationManager locationManager;
    private String provider;

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_location_update:
                checkAndRequestPermission();
                Location location = locationManager.getLastKnownLocation(provider);
                if(location!=null){
                    showLocation(location);
                }

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        10 * 1000, //10 sec
                        10,        //10 meter
                        locationListener);
                break;
            case R.id.stop_location_update:
                stopLocationListener();
                break;
            default:
                break;
        }
    }

    private void stopLocationListener(){
        if(locationManager!=null){
            try {
                locationManager.removeUpdates(locationListener);
            }catch (SecurityException ex){
                ex.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        startLocationUpdate = (Button)findViewById(R.id.start_location_update);
        stopLocationUpdate = (Button)findViewById(R.id.stop_location_update);
        positionTextView = (TextView)findViewById(R.id.position_text_view);
        startLocationUpdate.setOnClickListener(this);
        stopLocationUpdate.setOnClickListener(this);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        checkAndRequestPermission();

        List<String> providerList = locationManager.getProviders(true);
        if(providerList.contains(LocationManager.GPS_PROVIDER)){
            provider = LocationManager.GPS_PROVIDER;
        }
        else if (providerList.contains(LocationManager.NETWORK_PROVIDER)){
            provider = LocationManager.NETWORK_PROVIDER;
        }
        else {
            Toast.makeText(this, "No location provider to use", Toast.LENGTH_LONG).show();
            return;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        stopLocationListener();
    }

    private void showLocation(Location location){
        String currentPosition = location.getLatitude() + ", " + location.getLongitude();
        positionTextView.setText(currentPosition);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(this, "permission is granted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "permission is denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

    private void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please grant this App the permission to get LOCATION service at this device", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions( this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                    MY_PERMISSION_LOCATION );
        }

    }
}
