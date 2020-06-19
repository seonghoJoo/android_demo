package com.joo.corona.Util;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.NaverMap;

import java.util.Vector;

public class LatLngUtils {
    public final static double REFERANCE_LAT = 1/109.958489129649955;
    public final static double REFERANCE_LBG = 1/88.74;
    public final static double REFERENCE_LAT_X3 = 3/109.958489129649955;
    public final static double REFERENCE_LNG_X3 = 3/88.74;
    public static boolean withSignMarker(LatLng currentPosition, LatLng markerPosition){
        boolean withSignMarketLat = Math.abs(currentPosition.latitude - markerPosition.latitude) <=REFERENCE_LAT_X3;
        boolean withSignMarketLng = Math.abs(currentPosition.longitude - markerPosition.longitude) <=REFERENCE_LNG_X3;
        return withSignMarketLat && withSignMarketLng;
    }

    public static LatLng getcurrentPosition(NaverMap naverMap){
        CameraPosition cameraPosition = naverMap.getCameraPosition();
        return new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
    }
}
