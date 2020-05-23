package com.joo.corona;

import android.*;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.renderscript.Double2;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joo.corona.Adapter.GetCurrentLocationAdapter;
import com.joo.corona.Data.PrefM;
import com.joo.corona.Util.SharedPreferenceManager;
import com.kyleduo.switchbutton.SwitchButton;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static com.joo.corona.Util.SharedPreferenceManager.MY_LAT;
import static com.joo.corona.Util.SharedPreferenceManager.MY_LNG;

public class MainActivity extends BaseActivity {
    String TAG = "MainActivity";
    SwitchButton switchButton;
    private static final int REQUEST_PERMISSIONS = 100;

    boolean boolean_permission, first = true;
    LatLng myLatLng;
    LatLng cameraLatLng;
    Long b4Time = 0L;
    TextView tv_latitude, tv_longitude, tv_address, tv_onoff, tv_date;
    SharedPreferenceManager pref;
    PathOverlay path = new PathOverlay();
    Button myBtn, otherBtn;
    long now;
    Double latitude,longitude;
    String time;
    Geocoder geocoder;
    ImageView imageViewLeft, imageViewRight;
    FusedLocationSource locationSource;
    public static String filename = "gpsService.txt";
    int month,day,hour,minute, gpsCount,cnt;
    NaverMap mNaverMap;
    UiSettings naverMapUiSetting;
    File file, dir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cnt = PrefM.getInt(this,"rebuild");
        //값이 할당 전이면 -1을 띄운다
        Log.e(TAG,cnt+"");
        if(cnt==-1){
            PrefM.setInt(this,"rebuild",gpsCount);
        }

        tv_onoff = (TextView) findViewById(R.id.main_title_onoff);
        switchButton = (SwitchButton) findViewById(R.id.main_title_switch);
        myBtn = (Button) findViewById(R.id.main_my_btn);
        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now = System.currentTimeMillis();
                String getTime = getTimeString(now);
                String[] temp = getTime.split("-");
                int tempYear = Integer.parseInt(temp[0]);
                int tempMonth = Integer.parseInt(temp[1]);
                int tempDay = Integer.parseInt(temp[2]);
                load(tempYear,tempMonth,tempDay);
            }
        });
        otherBtn = (Button) findViewById(R.id.main_case_btn);
        otherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now = System.currentTimeMillis();
                String getTime = getTimeString(now);
                String[] temp = getTime.split("-");
                int tempYear = Integer.parseInt(temp[0]);
                int tempMonth = Integer.parseInt(temp[1]);
                int tempDay = Integer.parseInt(temp[2]);
                load(tempYear,tempMonth,tempDay);
            }
        });
        tv_date = (TextView) findViewById(R.id.main_date_tv);
        now = System.currentTimeMillis();
        String getTime = getTimeString(now);
        String[] temp = getTime.split("-");
        int tempYear = Integer.parseInt(temp[0]);
        int tempMonth = Integer.parseInt(temp[1]);
        int tempDay = Integer.parseInt(temp[2]);
        load(tempYear,tempMonth,tempDay);
        tv_date.setText(tempMonth + "월 " + tempDay +"일");

        imageViewLeft = (ImageView) findViewById(R.id.main_left_iv);
        imageViewLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path.setMap(null);
                now = now - 24*60*60*1000;
                Date mDate = new Date(now);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String getTime = simpleDateFormat.format(mDate);
                String[] temp = getTime.split("-");
                int tempYear = Integer.parseInt(temp[0]);
                int tempMonth = Integer.parseInt(temp[1]);
                int tempDay = Integer.parseInt(temp[2]);
                tv_date.setText(tempMonth + "월 " + tempDay +"일");
                Log.e(TAG,"LeftLoad");
                //load(tempYear,tempMonth,tempDay);
            }
        });
        imageViewRight = (ImageView) findViewById(R.id.main_right_iv);
        imageViewRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path.setMap(null);
                now = now + 24*60*60*1000;
                String getTime = getTimeString(now);
                String[] temp = getTime.split("-");
                int tempYear = Integer.parseInt(temp[0]);
                int tempMonth = Integer.parseInt(temp[1]);
                int tempDay = Integer.parseInt(temp[2]);
                tv_date.setText(tempMonth + "월 " + tempDay +"일");
                Log.e(TAG,"RigjtLoad");
                //load(tempYear,tempMonth,tempDay);
            }
        });

        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_latitude = (TextView) findViewById(R.id.tv_latitude);
        tv_longitude = (TextView) findViewById(R.id.tv_longitude);
        geocoder = new Geocoder(this, Locale.getDefault());


//        //세로방향 리사이클러뷰
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
//        recyclerView.setLayoutManager(layoutManager);
//        GetCurrentLocationAdapter adapter = new GetCurrentLocationAdapter(getApplicationContext());
//        recyclerView.setAdapter(adapter);

        // 스위치 버튼입니다.
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 스위치 버튼이 체크되었는지 검사하여 텍스트뷰에 각 경우에 맞게 출력합니다.
                if (isChecked){
                    if(boolean_permission) {
                        tv_onoff.setText("온라인");
//                        if (mPref.getString("service", "").matches("")) {
//                            medit.putString("service", "service").commit();
                            Intent intent = new Intent(MainActivity.this, GPSService.class);
                            intent.putExtra("runnung",0);
                            Log.e(TAG,"startActivity");
                            startService(intent);
                        //}
                    }

                }else{
                    Intent intent = new Intent(MainActivity.this, GPSService.class);
                    intent.putExtra("runnung",1);
                    Log.e(TAG,"endActivity");

                    stopService(intent);

                    tv_onoff.setText("오프라인");
                }
            }
        });



        fn_permission();
        setMapView();
    }

    // 네이버 맵 세팅
    private void setMapView() {
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("nl2lmc3avl"));
        // 로케이션 잡기 위한 변수
        locationSource = new FusedLocationSource(this, 1000);

        // 맵은 내부 프래그먼트변수로 사용하는것이 편함
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.main_mapview);
        if (mapFragment == null) {
            // 맵 프래그먼트 초기화
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.main_mapview, mapFragment).commit();
        }

        // 맵 프래그먼트에 맵 연결
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull NaverMap naverMap) {
                mNaverMap = naverMap;
                mNaverMap.setLocationSource(locationSource);
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
                // 맵을 현재 위치 따라오게 연결
                setMapUiSetting();
                setMapLocationChanged();
                setCameraChanged();
            }
        });

//        LatLng northWest = new LatLng(31.43,122.37);
//        LatLng southEast = new LatLng(44.35,132);
//        mNaverMap.setExtent(new LatLngBounds(northWest,southEast));

    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION

                        },
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));
            time = String.valueOf(intent.getStringExtra("AtTime"));
            long tmpeTime = Long.parseLong(time);

            Date mDate = new Date(tmpeTime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            String getTime = simpleDateFormat.format(mDate);
            String[] temp = getTime.split("-");
            String tempYear = temp[0];
            String tempMonth = temp[1];
            String tempDay = temp[2];
            String tempHour = temp[3];
            String tempMinute = temp[4];
//            int tempYear = Integer.parseInt(temp[0]);
//            int tempMonth = Integer.parseInt(temp[1]);
//            int tempDay = Integer.parseInt(temp[2]);
//            int tempHour = Integer.parseInt(temp[3]);
//            int tempMinute = Integer.parseInt(temp[4]);

            String fileToWrirte = tempYear+"/"+tempMonth+"/"+tempDay+"/"+tempHour+"/"+tempMinute +"/"+latitude+"/"+longitude+"/\n";

            try{
               BufferedWriter bw = new BufferedWriter((new FileWriter(getFilesDir()+filename,true)));
               bw.write(fileToWrirte);
               bw.close();
               Log.e(TAG,fileToWrirte);

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(GPSService.str_receiver));

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
    public void load (int tempYear,int tempMonth, int tempDay){
        try{
            BufferedReader br = new BufferedReader(new FileReader(getFilesDir()+filename));
            String text = null;
            double lat;
            double lng;
            Long t;
            Date today;
            Log.e(TAG,"Load text");

            LatLng tempGeo;
            List<LatLng> coords = new ArrayList<>();
           //List<LatLng> tempCoords = new ArrayList<>();
            while((text= br.readLine())!=null){

                String[] temp = text.split("/");
                Log.e(TAG,"tempAssigned");
                //Log.e(TAG,temp[0]+"    " + temp[1]+  "    " + temp[2]);
                int year = Integer.parseInt(temp[0]);
                int month = Integer.parseInt(temp[1]);
                int day = Integer.parseInt(temp[2]);
                int hour,minute;
                if(tempYear ==year && tempMonth == month && tempDay == day){
                    hour = Integer.parseInt(temp[3]);
                    minute =Integer.parseInt(temp[4]);
                    lat = Double.parseDouble(temp[5]);
                    lng = Double.parseDouble(temp[6]);
                    tempGeo = new LatLng(lat,lng);
                    Log.e(TAG, "lat: "+ lat + " lng: " + lng +"  " + month+"/"+day+"/"+hour+"/"+minute);
                    Collections.addAll(coords,new LatLng(lat,lng));
                }

            }
            path.setCoords(coords);
            path.setMap(mNaverMap);
            path.setWidth(10);
            path.setOutlineWidth(5);
            //path.setOutlineColor(333333);
            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setMapUiSetting(){
        // 맵 요소 세팅
        // 나침반, 현재위치 세팅
        // 기본 값으로 확대축소 축척 표시됨
        naverMapUiSetting = mNaverMap.getUiSettings();
        naverMapUiSetting.setCompassEnabled(true);
        naverMapUiSetting.setLocationButtonEnabled(true);
    }
    private void setMapLocationChanged() {
        mNaverMap.addOnLocationChangeListener(
                new NaverMap.OnLocationChangeListener() {
                    @Override
                    public void onLocationChange(@NonNull Location location) {
                        // 내위치 뼌경시 좌표 저장
                        myLatLng = new LatLng(
                                location.getLatitude(),
                                location.getLongitude());

                        if (first) {
                            // 첫번째 인 경우, 내 좌표로 카메라 이동
                            setMyLocation();
                        }
                    }
                });
    }
    private void setMyLocation() {
        // 내 좌표로 카메라 이동
        // 초기에 내 좌표로 이동

        first = false;
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(myLatLng);
        mNaverMap.moveCamera(cameraUpdate);
    }

    private void setCameraChanged() {
        mNaverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int reason, boolean animated) {

                // 지도 스크롤해서 보고있는 위치가 바뀐경우 호출
                // 지도 스크롤마다 reverseGeocoording 을 호출하는데
                // 호출 횟수를 줄이기위해, 0.3초마다 읽기 + 이전에 갱신한 위치와 10미터 이상 벗어난경우 읽기 기능추가

                Long changeTime = System.currentTimeMillis();
                if (changeTime - b4Time > 2000) { // 이전 변경에서 1초 이상이면
                    b4Time = System.currentTimeMillis();
                    try {
                        if (cameraLatLng.distanceTo(mNaverMap.getCameraPosition().target) > 10.0) { // 이전 변경에서 10미터 이상 이동하면
                            cameraLatLng = mNaverMap.getCameraPosition().target;
                            Log.e("camera", cameraLatLng.latitude + " " + cameraLatLng.longitude);

                        }
                    } catch (Exception e) {
                        cameraLatLng = mNaverMap.getCameraPosition().target;
                        Log.e("camera", cameraLatLng.latitude + " " + cameraLatLng.longitude);

                }
            }
        }});
    }
    private String getTimeString(long t){
        Date mDate = new Date(t);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(mDate);
    }
}