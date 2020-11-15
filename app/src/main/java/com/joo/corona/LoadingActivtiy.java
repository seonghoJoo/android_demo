package com.joo.corona;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

public class LoadingActivtiy extends BaseActivity {
    PermissionListener permissionListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_activtiy);
        Log.e("loading", "intent실행");
        permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(LoadingActivtiy.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                Log.e("Loading", "loading intent to MainActivity 실행");
                Intent intent = new Intent(LoadingActivtiy.this, MainActivity.class);
                Log.e("loading", "intent실행");
                startActivity(intent);
                Log.e("loading", "startActivity");
                finish();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(LoadingActivtiy.this, "필수권한에 동의하지 않으시면 앱을 사용할 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        };
        // 오픈소스 테드퍼미션을 이용해 필수권한 체크
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleTitle("앱 접근 권한 안내")
                .setRationaleMessage("권한 관련 메세지 안내 동의는 필수로 하셔야 이용하실 수 있습니다.")
                .setDeniedMessage(
                        "If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions( new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}
                )
                .check();


    }

}
