package com.joo.corona.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {

    private final String PREF_NAME = "myPref";

    public final static String MY_LAT = "MY_LAT"; // double
    public final static String MY_LNG = "MY_LNG"; // double

//    public final static String MY_DO = "MY_DO"; // String korean
//    public final static String MY_SI = "MY_SI"; // String korean
//
//    public final static String TEMP_ID = "TEMP_ID"; // String

    static Context context;

    public SharedPreferenceManager(Context context) {
        this.context = context;
    }

    public void put(String key, double value) {
        float fValue = (float)value;
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME
                , Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, fValue);
        editor.commit();
    }


    public double getValue(String key, double defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(
                PREF_NAME, Activity.MODE_PRIVATE);
        try{
            return (double)(pref.getFloat(key, (float)defaultValue));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public String getValue(String key, String defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(
                PREF_NAME, Activity.MODE_PRIVATE);
        try{
            return pref.getString(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
