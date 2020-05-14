package com.joo.corona.Data;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefM {
    public static final String PREFERENCE_NAME = "rebuilld_preference";
    private static final int DEFAULT_VALUE_INT=-1;
    public static SharedPreferences getPreferences(Context context){
        return context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

    public static void setInt(Context context, String key, int val){
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key,val);
        editor.commit();
    }
    public static int getInt(Context context, String key){
        SharedPreferences prefs = getPreferences(context);

        int val = prefs.getInt(key,DEFAULT_VALUE_INT);
        return val;
    }

}
