package com.joo.corona;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Output;
import android.os.AsyncTask;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;


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
    LocationManager b4LocationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 10*1000;
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;
    int val=0;
    // Constants
    double originLat, originLong;
    private static final int ID_SERVICE = 101;

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


        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(),5,notify_interval);
        Log.e("GPSService",mTimer.toString());
        intent = new Intent(str_receiver);
        fn_getlocation(val);

        // Create the Foreground Service
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(ID_SERVICE, notification);

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
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
        if(v==0) {
            if (locationManager == null) {
                locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            }
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnable || !isNetworkEnable) {
                if (!isNetworkEnable && isGPSEnable)
                    Log.e(TAG, "네트워크문제");
                if (isNetworkEnable && !isGPSEnable)
                    Log.e(TAG, "gps문제");
                if (!isNetworkEnable && !isGPSEnable)
                    Log.e(TAG, "둘다 문제");
                try{
                    mTimer.cancel();
                    mTimer = null;
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                location = null;
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000, 3, this);

                if (locationManager != null) {
                    //b4LocationManager = locationManager;
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    //3 meter 좌표 사이 거리 설정
                    if (distance(originLat, originLong, location.getLatitude(), location.getLongitude()) > 3.0 && location != null) {
                        originLat = location.getLatitude();
                        originLong = location.getLongitude();
                        Toast.makeText(getApplicationContext(), location.getTime() + "", Toast.LENGTH_LONG).show();
                        fn_update(location);
                    }
                }

            }
        }else{
            mTimer.cancel();
            mTimer = null;
        }

    }

    private void fn_update(Location location){

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
                    if(val==0)
                    fn_getlocation(val);;

                }
            });

        }
    }
    private double distance(double lat1, double lng1, double lat2, double lng2 ){
        double theta = lng1 - lng2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1609.344;
        return dist;
    }
    private double deg2rad(double deg){
        return deg*Math.PI / 180;
    }

    private double rad2deg(double rad){
        return rad* 180 / Math.PI;
    }

}