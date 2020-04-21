package com.joo.corona;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.joo.corona.Service.MyBackgroundLocationService;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Long timeDelay = 1000 * 2 * 600L;
    float distanceDelay = 30.0f;
    LocationManager locationManager;
    RelativeLayout relativeLayout;
    Switch gpsSwitch;
    TextView textViewMain;

    FusedLocationSource locationSource;
    UiSettings naverMapUiSetting;
    NaverMap mNaverMap;
    Long b4Time = 0L;
    LatLng myLatLng;
    LatLng cameraLatLng;
    private boolean first = true;
    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout = (RelativeLayout) findViewById(R.id.main_relative);
        gpsSwitch = (Switch) findViewById(R.id.main_switch);
        gpsSwitch.setOnCheckedChangeListener(new gpsSwitchListener());

    }
    class gpsSwitchListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                textViewMain.setText("온라인");
                Intent intent = new Intent(getApplicationContext(), MyBackgroundLocationService.class);
                startService(intent);
                Toast.makeText(getApplicationContext(),"service Started",Toast.LENGTH_LONG).show();
            }
            else {
                textViewMain.setText("오프라인");
                Intent intent = new Intent(getApplicationContext(), MyBackgroundLocationService.class);
                stopService(intent);
                Toast.makeText(getApplicationContext(), "service Stopped", Toast.LENGTH_LONG).show();
            }
        }
    }

    // 네이버 맵 세팅
    private void setMapView() {

        // 로케이션 잡기 위한 변수
        locationSource = new FusedLocationSource(this, 1000);

        // 맵은 내부 프래그먼트변수로 사용하는것이 편함
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            // 맵 프래그먼트 초기화
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }

        // 맵 프래그먼트에 맵 연결
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull NaverMap naverMap) {
                mNaverMap = naverMap;
                mNaverMap.setLocationSource(locationSource);
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

                // 카메라 영역 제한
                LatLng northWest = new LatLng(31.43, 122.37);   //서북단
                LatLng southEast = new LatLng(44.35, 132);      //동남단
                naverMap.setExtent(new LatLngBounds(northWest, southEast));


                // 맵을 현재 위치 따라오게 연결
                setMapUiSetting();
                //setMapLocationChanged();
                setCameraChanged();
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


    private void setMapUiSetting() {
        // 맵 요소 세팅
        // 나침반, 현재위치 세팅
        // 기본 값으로 확대축소 축척 표시됨
        naverMapUiSetting = mNaverMap.getUiSettings();
        naverMapUiSetting.setCompassEnabled(true);
        naverMapUiSetting.setLocationButtonEnabled(true);
    }


    private void setCameraChanged() {
        mNaverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int reason, boolean animated) {

                // 지도 스크롤해서 보고있는 위치가 바뀐경우 호출
                // 지도 스크롤마다 reverseGeocoording 을 호출하는데
                // 호출 횟수를 줄이기위해, 1초마다 읽기 + 이전에 갱신한 위치와 10미터 이상 벗어난경우 읽기 기능추가

                Long changeTime = System.currentTimeMillis();
                if (changeTime - b4Time > 1000) { // 이전 변경에서 1초 이상이면
                    b4Time = System.currentTimeMillis();
                    try {
                        if (cameraLatLng.distanceTo(mNaverMap.getCameraPosition().target) > 30.0) { // 이전 변경에서 30미터 이상 이동하면
                            cameraLatLng = mNaverMap.getCameraPosition().target;
                            Log.e("camera", cameraLatLng.latitude + " " + cameraLatLng.longitude);
                            getGeoCoording(
                                    String.valueOf(cameraLatLng.latitude),
                                    String.valueOf(cameraLatLng.longitude),
                                    MainActivity.this);
                        }
                    } catch (Exception e) {
                        cameraLatLng = mNaverMap.getCameraPosition().target;
                        Log.e("camera", cameraLatLng.latitude + " " + cameraLatLng.longitude);
                        getGeoCoording(
                                String.valueOf(cameraLatLng.latitude),
                                String.valueOf(cameraLatLng.longitude),
                                MainActivity.this);
                    }
                }
            }
        });
    }
//
//    private void setMapLocationChanged() {
//        mNaverMap.addOnLocationChangeListener(
//                new NaverMap.OnLocationChangeListener() {
//                    @Override
//                    public void onLocationChange(@NonNull Location location) {
//                        // 내위치 뼌경시 좌표 저장
//                        myLatLng = new LatLng(
//                                location.getLatitude(),
//                                location.getLongitude());
//
//                        pref.put(pref.MY_LAT, location.getLatitude());
//                        pref.put(pref.MY_LNG, location.getLongitude());
//
//                        if (first) {
//                            // 첫번째 인 경우, 내 좌표로 카메라 이동
//                            setMyLocation();
//                        }
//                    }
//                });
//    }


}
