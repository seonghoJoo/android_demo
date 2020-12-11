package com.joo.corona;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.joo.corona.Data.Ccase;
import com.joo.corona.Data.PrefM;
import com.joo.corona.Utils.AppHelper;
import com.joo.corona.Utils.LatLngUtils;
import com.joo.corona.Utils.SharedPreferenceManager;
import com.joo.corona.Utils.TimeUtil;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends BaseActivity {


    private static final int REQUEST_PERMISSIONS = 100;
    private static String filename = "gpsService.txt";
    public static long b4Time = 0L;

    boolean boolean_permission, first = true;
    int gpsCount,cnt;
    long now;

    Double latitude,longitude;
    String TAG = "MainActivity";
    String time;
    LatLng myLatLng;
    LatLng cameraLatLng;

    List<Ccase> caseList = new ArrayList<>();
    List<LatLng> coords = new ArrayList<>();
    Geocoder geocoder;
    FusedLocationSource locationSource;

    NaverMap mNaverMap;
    Button myBtn, otherBtn;
    ImageView imageViewLeft, imageViewRight;
    LinearLayout panelRoot;
    Marker casemarker = new Marker();
    Switch switchButton;
    TextView tv_latitude, tv_longitude, tv_address, tv_onoff, tv_date , tv_panel_address, tv_panel_place , tv_panel_visit, tv_panel_end,tv_panel_mask;
    UiSettings naverMapUiSetting;
    ArrayList<Marker> casemarkerArrayList = new ArrayList<>();
    ArrayList<Marker> mymarkerArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_date = (TextView) findViewById(R.id.main_date_tv);
        tv_onoff = (TextView) findViewById(R.id.main_title_onoff);
        switchButton = (Switch) findViewById(R.id.main_title_switch);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_latitude = (TextView) findViewById(R.id.tv_latitude);
        tv_longitude = (TextView) findViewById(R.id.tv_longitude);
        panelRoot = (LinearLayout) findViewById(R.id.main_panel_ll_root);
        tv_panel_address = (TextView) findViewById(R.id.main_c_address_tv);
        tv_panel_place = (TextView) findViewById(R.id.main_place_tv);
        tv_panel_visit = (TextView) findViewById(R.id.main_tv_visit_time_tv);
        tv_panel_end = (TextView) findViewById(R.id.main_tv_end_time_tv);
        tv_panel_mask = (TextView) findViewById(R.id.main_mask_tv);
        Log.e(TAG,"onCreate");

        now = System.currentTimeMillis();
        //String getTime = getTimeString(now);
        String[] temp = TimeUtil.getCurrentTimeStringArray(now);
        Log.e("Main",temp[1]+ "월 " + temp[2] +"일");
        tv_date.setText(temp[1] + "월 " + temp[2] +"일");

        cnt = PrefM.getInt(this,"rebuild");
        //값이 할당 전이면 -1을 띄운다
        Log.e(TAG,cnt+"");
        if(cnt==-1){
            PrefM.setInt(this,"rebuild",gpsCount);
        }

        myBtn = (Button) findViewById(R.id.main_my_btn);
        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timeChange(2000)){
                    getCurrentTime(now,0);
                }
            }
        });
        otherBtn = (Button) findViewById(R.id.main_case_btn);
        otherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timeChange(2000)){
                    getCurrentTime(now,1);
                    if(AppHelper.requestQueue == null) {
                        AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
                    }
                }
            }
        });
        imageViewLeft = (ImageView) findViewById(R.id.main_left_iv);
        imageViewLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now = now - 24*60*60*1000;
                String[] temp = TimeUtil.getCurrentTimeStringArray(now);
                tv_date.setText(temp[1] + "월 " + temp[2] +"일");
                Log.e(TAG,"LeftLoad");
            }
        });
        imageViewRight = (ImageView) findViewById(R.id.main_right_iv);
        imageViewRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caseList.clear();
                now = now + 24*60*60*1000;
                String[] temp = TimeUtil.getCurrentTimeStringArray(now);
                tv_date.setText(temp[1] + "월 " + temp[2] +"일");
                Log.e(TAG,"RightLoad");
                //load(tempYear,tempMonth,tempDay);
            }
        });

        geocoder = new Geocoder(this, Locale.getDefault());
        Boolean startBoolean = SharedPreferenceManager.getBoolean(getApplicationContext(),"rebuild");
        tv_onoff.setText((startBoolean==true? "온" : "오프")+"라인");
        switchButton.setChecked(startBoolean);
        boolean_permission = startBoolean;
        // 스위치 버튼입니다.
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 스위치 버튼이 체크되었는지 검사하여 텍스트뷰에 각 경우에 맞게 출력합니다.
                if (isChecked){
                    if(boolean_permission) {
                        SharedPreferenceManager.setBoolean(getApplicationContext(),"rebuild",true);
                        tv_onoff.setText("온라인");
                        Intent intent = new Intent(MainActivity.this, GPSService.class);
                        intent.putExtra("runnung",0);
                        Log.e(TAG,"startActivity");
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            Log.e(TAG,"foreground 시작");
                            startForegroundService(intent);
                        }else{
                            Log.e(TAG,"background 시작");
                            startService(intent);
                        }
                    }
                }else{
                    Intent intent = new Intent(MainActivity.this, GPSService.class);
                    intent.putExtra("runnung",1);
                    Log.e(TAG,"endActivity");
                    stopService(intent);
                    SharedPreferenceManager.setBoolean(getApplicationContext(),"rebuild",false);
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
    }
    //권한 체크
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
    //권한 체크 결과
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
    //GPSService 결과를 받는 BroadCaster
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));

            //String -> Long 시간을 Long 타입으로 변환
            time = String.valueOf(intent.getStringExtra("AtTime"));
            long tempTime = Long.parseLong(time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            String getTime = simpleDateFormat.format(tempTime);
            String[] temp = getTime.split("-");
            String tempYear = temp[0];
            String tempMonth = temp[1];
            String tempDay = temp[2];
            String tempHour = temp[3];
            String tempMinute = temp[4];

            String fileToWrirte = tempYear+"/"+tempMonth+"/"+tempDay+"/"+tempHour+"/"+tempMinute +"/"+latitude+"/"+longitude+"/\n";
            // 파일 쓰기
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
    public void sendRequest(int year, int month, int day){
        // 서버에서 Json 요청하기
        String url = "http://18.217.248.147:3000/getposts_date";
        Log.e("Main","서버 요");
        RequestQueue postReQuestQueue = Volley.newRequestQueue(this);
        try{
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                              Log.e(TAG,"응답: "+ response);
                              processResponse(response);
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG,"에러: "+ error.getMessage());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams(){
                            Map<String, String> myData = new HashMap<>();
                            myData.put("year", String.valueOf(year));
                            myData.put("month", String.valueOf(month));
                            myData.put("day", String.valueOf(day));
                            return myData;
                        }
                    };
            postReQuestQueue.add(request);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public void processResponse(String response){
        Gson gson = new Gson();
        Log.e(TAG,"processResponse() 실행");

        Ccase[] array = gson.fromJson(response,Ccase[].class);
        caseList = Arrays.asList(array);

    }
    public void getCurrentTime(long now,int flag){
        String getTime = getTimeString(now);
        String[] temp = getTime.split("-");
        int tempYear = Integer.parseInt(temp[0]);
        int tempMonth = Integer.parseInt(temp[1]);
        int tempDay = Integer.parseInt(temp[2]);
        if(flag==0){
            load(tempYear,tempMonth,tempDay);
        }else{
            loadJSON(tempYear,tempMonth,tempDay);
        }

    }
    public void loadJSON(int tempYear,int tempMonth, int tempDay){

        sendRequest(tempYear,tempMonth,tempDay);
        int i = 0;
//        while(i<caseList.size()) {
//            casemarkerArrayList.get(i).setMap(null);
//            i++;
//        }
//        i=0;
        casemarkerArrayList.clear();
        casemarker.setMap(null);

        while (i < caseList.size()) {
            Log.e(TAG,"i: " + i);
            casemarker = new Marker();
            Log.e(TAG, caseList.get(i).getLongtitude());
            casemarker.setPosition(new LatLng(Double.parseDouble(caseList.get(i).getLatitude()), Double.parseDouble(caseList.get(i).getLongtitude())));
            casemarker.setIcon(OverlayImage.fromResource(R.drawable._case_marker));

            casemarker.setCaptionText(caseList.get(i).getPlace_name());
            casemarker.setSubCaptionText(caseList.get(i).getC_address() + " 확진자");
            casemarker.setSubCaptionColor(Color.BLUE);
            casemarker.setSubCaptionHaloColor(Color.rgb(200, 255, 200));
            casemarker.setSubCaptionTextSize(10);

            if(caseList.get(i).getMask()==1) {
                casemarker.setIconTintColor(0x00CC00);
            }else{
                casemarker.setIconTintColor(Color.RED);
            }
            casemarker.setCaptionRequestedWidth(200);
            // 마커에 태그부여
            casemarker.setTag(i);
            // 맵에 마커 위치시킴
            casemarker.setMap(mNaverMap);

            // 마커에 클릭함수 셋
            //marker.setOnClickListener(markerOnClickListener);
            casemarkerArrayList.add(casemarker);
            i++;
        }
        //marker.setPosition();

    }



    public void load (int tempYear, int tempMonth, int tempDay){
        try{
            BufferedReader br = new BufferedReader(new FileReader(getFilesDir()+filename));
            String text = null;
            double lat;
            double lng;
            Long t;
            Date today;
            for (Marker m : mymarkerArrayList) {
                m.setMap(null);
                // 마커를 지도에서 지움
            }
            mymarkerArrayList.clear();

            Log.e(TAG,"Load text");

           //List<LatLng> tempCoords = new ArrayList<>();
            int flag=0;
            int hour,minute;
            int cnt=0;

            while((text= br.readLine())!=null){
                String[] temp = text.split("/");
                int year = Integer.parseInt(temp[0]);
                int month = Integer.parseInt(temp[1]);
                int day = Integer.parseInt(temp[2]);
                if(tempYear == year && tempMonth == month && tempDay == day){
                    flag=1;
                    hour = Integer.parseInt(temp[3]);
                    minute =Integer.parseInt(temp[4]);
                    lat = Double.parseDouble(temp[5]);
                    lng = Double.parseDouble(temp[6]);
                    try{
                        Marker myMarker = new Marker();
                        myMarker.setPosition(new LatLng(lat, lng));
                        //marker.setIcon(MarkerIcons.BLACK);
                        myMarker.setIcon(OverlayImage.fromResource(R.drawable._case_marker));
                        //마스크 착용
                        switch (hour/2){
                            case 1:
                                myMarker.setIconTintColor(0x006666);
                                break;
                            case 2:
                                myMarker.setIconTintColor(0x009999);
                                break;
                            case 3:
                                myMarker.setIconTintColor(0x0CCCCC);
                                break;
                            case 4:
                                myMarker.setIconTintColor(0x00FFFF);
                                break;
                            case 5:
                                myMarker.setIconTintColor(0x33FFFF);
                                break;
                            case 6:
                                myMarker.setIconTintColor(0x66FFFF);
                                break;
                            case 7:
                                myMarker.setIconTintColor(0x000099);
                                break;
                            case 8:
                                myMarker.setIconTintColor(0x0000CC);
                                break;
                            case 9:
                                myMarker.setIconTintColor(0x0000FF);
                                break;
                            case 10:
                                myMarker.setIconTintColor(0x3333FF);
                                break;
                            case 11:
                                myMarker.setIconTintColor(0x6666FF);
                                break;
                            case 12:
                                myMarker.setIconTintColor(0x9999FF);
                                break;
                            default:
                                myMarker.setIconTintColor(0xCC99FF);
                                break;
                        }

                        // 마커에 태그부여
                        myMarker.setTag(cnt);
                        cnt++;
                        // 맵에 마커 위치시킴
                        myMarker.setMap(mNaverMap);
                        // 마커에 클릭함수 셋
                        //marker.setOnClickListener(markerOnClickListener);
                        // 마커에 클릭함수 셋
                        //marker.setOnClickListener(markerOnClickListener);
                        mymarkerArrayList.add(myMarker);
                        //activeMarkers.add(myMarker);
                        Log.e(TAG, "lat: "+ lat + " lng: " + lng +"  " + month+"/"+day+"/"+hour+"/"+minute);
                        Collections.addAll(coords,new LatLng(lat,lng));
                    }
                    catch (Exception e){

                    }
                }
            }
            if(coords.size()<2 || flag==0){

            }
            else if(coords.size()>=2 && flag==1) {
                coords.clear();
                Toast.makeText(getApplicationContext(),"현재 "+ cnt +"개의 동선이 있습니다.",Toast.LENGTH_LONG).show();
//                int i=0;
//                while(i<mymarkerArrayList.size()){
//                    mymarkerArrayList.get(i).setMap(mNaverMap);
//                    i++;
//                }
            }
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
                if(timeChange(2000)){
                    try {
                        if (cameraLatLng.distanceTo(mNaverMap.getCameraPosition().target) > 100.0) { // 이전 변경에서 10미터 이상 이동하면
                            cameraLatLng = mNaverMap.getCameraPosition().target;
                            Log.e("camera", cameraLatLng.latitude + " " + cameraLatLng.longitude);
                            LatLng currentPosition = LatLngUtils.getcurrentPosition(mNaverMap);
                            int i=0;
                            for (LatLng markerPostion: coords){
                                if(!LatLngUtils.withSignMarker(currentPosition,markerPostion)) {
                                    freeActiveMarkers(i);
                                    continue;
                                }
                                Marker marker = new Marker();
                                marker = mymarkerArrayList.get(i);
                                marker.setMap(mNaverMap);
                                i++;
                                Log.e(TAG,"i: "+ i);
                            }
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
    private boolean timeChange(long duration){
        Long changeTime = System.currentTimeMillis();
        if(changeTime - b4Time >duration){
            b4Time = System.currentTimeMillis();
            return true;
        }
        return false;
    }
    private void freeActiveMarkers(int order){
        if(mymarkerArrayList == null){
            mymarkerArrayList = new ArrayList<Marker>();
            return;
        }
//        for (Marker activeMarker:  mymarkerArrayList){
//            activeMarker.setMap(null);
//        }
//        mymarkerArrayList = new ArrayList<Marker>();
        mymarkerArrayList.get(order).setMap(null);
    }
}