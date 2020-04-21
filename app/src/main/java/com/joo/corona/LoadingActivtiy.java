package com.joo.corona;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class LoadingActivtiy extends AppCompatActivity {

    // 필수권한 체크
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Intent intent = new Intent(LoadingActivtiy.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(LoadingActivtiy.this, "필수권한에 동의하지 않으시면 앱을 사용할 수 없습니다", Toast.LENGTH_SHORT).show();
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_activtiy);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("알림")
                .setMessage("이 애플리케이션은 GPS 기반하여 코로나 바이러스 확진자와 접촉 가능성 여부를 알려줍니. \n모두 동의 하셔야 이용할 수 있습니다. 이에 동의하십니까?")
                .setPositiveButton("동의합니다", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 1초간 로딩화면을 띄워주고 권한을 체크하도록 구현
                        RunMainActivityAsynctask runMainActivityAsynctask
                                = new RunMainActivityAsynctask();
                        runMainActivityAsynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                })
                .setNegativeButton("종료하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SystemExit();
                    }
                });
        AlertDialog dialog = builder.create(); // 다이얼로그 생성
        dialog.show(); // 다이얼로그 출력
    }

    public class RunMainActivityAsynctask extends
            AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // 밀리세컨드단위. 1초간 대기
                // Thread sleep은 무조건 try catch문안에 넣어야한다.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 1초 대기 후 필수 권한 체크 시도
            permissionCheck();

        }
    }

    private void permissionCheck() {
        // 오픈소스 테드퍼미션을 이용해 필수권한 체크
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }
    public void SystemExit() {
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
