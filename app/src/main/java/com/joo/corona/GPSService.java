package com.joo.corona;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Output;
import android.os.AsyncTask;
import android.os.IBinder;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by deepshikha on 24/11/16.
 */

public class GPSService extends Service implements LocationListener {
    String TAG = "GPSServicee";
    public static String filename = "gpsService.txt";
    File file;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 5000;
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;
    int val=0;

    public GPSService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(TAG,"onCreate: " + val+"\n\n\n");
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(),5,notify_interval);
        intent = new Intent(str_receiver);
//        fn_getlocation();
    }

    @Override
    public void onDestroy() {
        if(this!=null && locationManager!=null){
            locationManager.removeUpdates(this);
        }
        locationManager = null;
        val=1;

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        val = intent.getIntExtra("running",0);
        Log.e(TAG,"onStartCommand val: "+ val);
        fn_getlocation(val);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onLocationChanged(Location location) {

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

    private void fn_getlocation(int v) {
        if (v == 0) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnable || !isNetworkEnable) {
                if(!isNetworkEnable && isGPSEnable)
                    Log.e(TAG,"네트워크문제");
                if(isNetworkEnable && !isGPSEnable)
                    Log.e(TAG,"gps문제");
                if(!isNetworkEnable && !isGPSEnable)
                    Log.e(TAG,"둘다 문");
            } else {
                location = null;
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100f, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                            Log.e("latitude", location.getLatitude() + "");
                            Log.e("longitude", location.getLongitude() + "");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);
                    }
                }

            }

        }
    }

    private void fn_update(Location location){
//        Log.e(TAG,location.getLatitude()+"gggg" +
//            "\n");
        intent.putExtra("latutide",location.getLatitude()+"");
        intent.putExtra("longitude",location.getLongitude()+"");
        intent.putExtra("AtTime",location.getTime()+"");
        sendBroadcast(intent);

    }
    private class TimerTaskToGetLocation extends TimerTask{
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(val==0){
                        fn_getlocation(val);;
                    }
                }
            });

        }
    }

}