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
import android.os.Environment;
import android.preference.PreferenceManager;
import android.renderscript.Double2;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joo.corona.Adapter.GetCurrentLocationAdapter;
import com.joo.corona.Data.PrefM;
import com.kyleduo.switchbutton.SwitchButton;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity {
    String TAG = "MainActivity";
    SwitchButton switchButton;
    private static final int REQUEST_PERMISSIONS = 100;

    boolean boolean_permission;
    TextView tv_latitude, tv_longitude, tv_address, tv_onoff;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    Double latitude,longitude;
    String time;
    Geocoder geocoder;
    Button button, deleteButton;
    FusedLocationSource locationSource;
    public static String filename = "gpsService.txt";
    int month,day,hour,minute, gpsCount,cnt;
    //NaverMap mNaverMap;
    //UiSettings naverMapUiSetting;
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
        deleteButton = (Button) findViewById(R.id.btn_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = getFilesDir();
                File file = new File(dir, filename);
                boolean deleted = file.delete();
            }
        });

        button = (Button) findViewById(R.id.btn_show);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG,"Load");
                load();
            }
        });
      //  setMapView();
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_latitude = (TextView) findViewById(R.id.tv_latitude);
        tv_longitude = (TextView) findViewById(R.id.tv_longitude);
        geocoder = new Geocoder(this, Locale.getDefault());
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();

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
    }

    // 네이버 맵 세팅
    private void setMapView() {

        // 로케이션 잡기 위한 변수
        locationSource = new FusedLocationSource(this, 1000);

        // 맵은 내부 프래그먼트변수로 사용하는것이 편함
        //MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.main_mapview);
//        if (mapFragment == null) {
//            // 맵 프래그먼트 초기화
//            mapFragment = MapFragment.newInstance();
//            getSupportFragmentManager().beginTransaction().add(R.id.main_mapview, mapFragment).commit();
//        }

//        // 맵 프래그먼트에 맵 연결
//        mapFragment.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull NaverMap naverMap) {
//                mNaverMap = naverMap;
//                mNaverMap.setLocationSource(locationSource);
//                mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
//                // 맵을 현재 위치 따라오게 연결
//                setMapUiSetting();
//                setMapLocationChanged();
//                setCameraChanged();
//            }
//        });
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

            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date timeDate = new Date(tmpeTime);
            //String timeInFormat = sdf.format(timeDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(timeDate);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);

            String fileToWrirte = month+"/"+day+"/"+hour+"/"+minute+"/"+latitude+"/"+longitude+"/\n";
            //Log.e(TAG,"MainActivity Lat:" + latitude + " Lng: " +  longitude + " time : " + time);
// Find the root of the external storage.

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
    public void load (){
        try{
            BufferedReader br = new BufferedReader(new FileReader(getFilesDir()+filename));
            String text = null;
            double lat;
            double lng;
            Long t;
            Date today;
            Log.e(TAG,"Load text");
            while((text= br.readLine())!=null){

                String[] temp = text.split("/");
                Log.e(TAG,"tempAssigned");
                //Log.e(TAG,temp[0]+"    " + temp[1]+  "    " + temp[2]);

                int month = Integer.parseInt(temp[0]);
                int day = Integer.parseInt(temp[1]);
                int hour = Integer.parseInt(temp[2]);
                int minute =Integer.parseInt(temp[3]);
                lat = Double.parseDouble(temp[4]);
                lng = Double.parseDouble(temp[5]);

                Log.e(TAG, "lat: "+ lat + " lng: " + lng +"  " + month+"/"+day+"/"+hour+"/"+minute);
            }
            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    private void setMapUiSetting(){
//        // 맵 요소 세팅
//        // 나침반, 현재위치 세팅
//        // 기본 값으로 확대축소 축척 표시됨
//
//        naverMapUiSetting = mNaverMap.getUiSettings();
//        naverMapUiSetting.setCompassEnabled(true);
//        naverMapUiSetting.setLocationButtonEnabled(true);
//    }
}