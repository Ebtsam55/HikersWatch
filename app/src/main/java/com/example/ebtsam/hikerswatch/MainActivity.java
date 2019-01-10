package com.example.ebtsam.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    TextView longitude;
    TextView latitude;
    TextView accuracy;
    TextView altitude;
    TextView address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        longitude=findViewById(R.id.longitude_text);
        latitude=findViewById(R.id.latitude_text);
        accuracy=findViewById(R.id.accuracy_text);
        altitude=findViewById(R.id.altitude_text);
        address=findViewById(R.id.address_text);

        locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

               updateLocationInfo(location);

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


        if(Build.VERSION.SDK_INT<23)
        {
           startListening();
        }
        else {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location!=null)
                {
                    updateLocationInfo(location);
                }
            }
        }



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
              startListening();
        }
    }

    public void startListening ()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }

    public  void updateLocationInfo(Location location)
    {   String address_str ="Could not find address";
        Log.i("Location Info",location.toString());
        longitude.setText("Longitude :"+String.valueOf(location.getLongitude()));
        latitude.setText("Latitude :"+String.valueOf(location.getLatitude()));
        altitude.setText("Altitude :"+String.valueOf(location.getAltitude()));
        accuracy.setText("Accuracy :"+String.valueOf(location.getAccuracy()));

        Geocoder geocoder =new Geocoder( getApplicationContext(),Locale.getDefault());
        try {
            List<Address> listAddress =geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if (listAddress!=null&&listAddress.size()>0)
            {   address_str="";
                Log.i("LocationAddress",listAddress.get(0).toString());
                if(listAddress.get(0).getSubThoroughfare()!=null)
                {
                    address_str +=listAddress.get(0).getSubThoroughfare()+"\n";
                }
                if(listAddress.get(0).getLocality()!=null)
                {
                    address_str +=listAddress.get(0).getLocality()+"\n";
                }

                if(listAddress.get(0).getPostalCode()!=null)
                {
                    address_str +=listAddress.get(0).getPostalCode()+"\n";
                }

                if(listAddress.get(0).getCountryName()!=null)
                {
                    address_str +=listAddress.get(0).getCountryName()+"\n";
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        address.setText("Address: \n"+address_str);
    }
}
