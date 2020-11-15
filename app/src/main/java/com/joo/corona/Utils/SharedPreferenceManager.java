package com.joo.corona.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {

    private static final String PREF_NAME = "myPref";

    public final static String MY_LAT = "MY_LAT"; // double
    public final static String MY_LNG = "MY_LNG"; // double
    public static final Boolean DEFUALT_VALUE_BOOLEAN = false;
//    public final static String MY_DO = "MY_DO"; // String korean
//    public final static String MY_SI = "MY_SI"; // String korean
//
//    public final static String TEMP_ID = "TEMP_ID"; // String

    static Context context;

    public SharedPreferenceManager(Context context) {
        this.context = context;
    }
    private static SharedPreferences getPreferences(Context context){
        return context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
    }
    public static boolean getBoolean(Context context,String key){
        SharedPreferences prefs = getPreferences(context);
        boolean value = prefs.getBoolean(key,DEFUALT_VALUE_BOOLEAN);
        return value;
    }
    public static void setBoolean(Context context, String key, boolean val)
    {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key,val);
        editor.commit();

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
    public static void clear(Context context){
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

}
