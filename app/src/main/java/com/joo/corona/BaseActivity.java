package com.joo.corona;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    public void setCustomStatusBar() {
        // manifest 에서 noActionBar 를 선택한 경우, 스테이터스바에 colorPrimaryDark 가 적용되지 않아, 강제 적용
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //BaseActivity를 상속받는 모든 액티비티의 스테이터스바 색상 강제지정
        setCustomStatusBar();
    }

}
