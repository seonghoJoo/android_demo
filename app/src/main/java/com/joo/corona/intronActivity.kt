package com.joo.corona

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat.postDelayed
import java.util.logging.Handler

class intronActivity : AppCompatActivity() {
    var handler : Handler? = null
    var runnable : Runnable? = null

    companion object {
        private const val REQUEST_LOCATION_PERMISSION_CODE = 100
    }

    fun checkLocationPermission() : Boolean{
        val fineLocationPermission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        return fineLocationPermission && coarseLocationPermission
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
    }
    fun moveMainActivity(){
        runnable = Runnable {
            val intent = Intent(applicationContext, MainActivity::class)
            startActivity(intent)
            finish()
        }
        handler = Handler()
        handler?.run {
            postDelayed(runnable, 2000)
        }
    }

    override fun onResume() {
        super.onResume()
        if(checkLocationPermission()) {
            moveMainActivity()
        }
        else
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                Toast.makeText(this,
                        "이 앱을 실행하려면 위치 권한이 필요합니다.", Toast.LENGTH_LONG).show()
            }

            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_LOCATION_PERMISSION_CODE)
        }
    }

    override fun onPause() {
        super.onPause()

        handler?.removeCallbacks(runnable)
    }

}