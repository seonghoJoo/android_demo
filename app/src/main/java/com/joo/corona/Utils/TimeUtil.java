package com.joo.corona.Utils;

import com.joo.corona.MainActivity;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class TimeUtil {

    public static String getTimeString(long t){
        Date mDate = new Date(t);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(mDate);
    }
    public static boolean timeChange(long duration){
        Long changeTime = System.currentTimeMillis();
        if(changeTime - MainActivity.b4Time >duration){
            MainActivity.b4Time = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public static String[] getCurrentTimeStringArray(long now){
        String getTime = getTimeString(now);
        String[] temp = getTime.split("-");
        int tempYear = Integer.parseInt(temp[0]);
        int tempMonth = Integer.parseInt(temp[1]);
        int tempDay = Integer.parseInt(temp[2]);
        return temp;
    }
}
