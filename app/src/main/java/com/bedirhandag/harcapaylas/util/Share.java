package com.bedirhandag.harcapaylas.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by MuratCan on 2019-10-17.
 */

public class Share {

    private static final String PREF_NAME = "__Share";
    private Context context = null;
    private SharedPreferences preferences = null;
    private static Share ourInstance = null;

    public static void setInstance(Context context) {
        Share share = new Share();
        share.context = context;
        share.preferences = share.context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        ourInstance = share;
    }

    private Share() {
    }

    public static void put(String key, String val) {
        SharedPreferences.Editor prefEdit = ourInstance.preferences.edit();
        prefEdit.putString(key, val);
        prefEdit.apply();
    }

    public static void put(String key, Object val) {
        SharedPreferences.Editor prefEdit = ourInstance.preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(val);
        prefEdit.putString(key, json);
        prefEdit.apply();
    }

    public static void put(String key, Boolean val) {
        SharedPreferences.Editor prefEdit = ourInstance.preferences.edit();
        prefEdit.putBoolean(key, val);
        prefEdit.apply();
    }

    public static void put(String key, int val) {
        SharedPreferences.Editor prefEdit = ourInstance.preferences.edit();
        prefEdit.putInt(key, val);
        prefEdit.apply();
    }

    public static void put(String key, Float val) {
        SharedPreferences.Editor prefEdit = ourInstance.preferences.edit();
        prefEdit.putFloat(key, val);
        prefEdit.apply();
    }

    public static void put(String key, Long val) {
        SharedPreferences.Editor prefEdit = ourInstance.preferences.edit();
        prefEdit.putLong(key, val);
        prefEdit.apply();
    }

    public static String getString(String key, String defaultValue) {
        return ourInstance.preferences.getString(key, defaultValue);
    }

    public static Object getObject(String key, String defaultValue, Class sClassName) {
        Gson gson = new Gson();
        String json = ourInstance.preferences.getString(key, defaultValue);
        return gson.fromJson(json, sClassName);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return ourInstance.preferences.getBoolean(key, defaultValue);
    }

    public static Float getFloat(String key, float defaultValue) {
        return ourInstance.preferences.getFloat(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        return ourInstance.preferences.getInt(key, defaultValue);
    }

    public static Long getLong(String key, long defaultValue) {
        return ourInstance.preferences.getLong(key, defaultValue);
    }

    public static void remove(String key) {
        ourInstance.preferences.edit().remove(key).apply();
        ourInstance.preferences.edit().apply();
    }

    public static void removeAll() {
        ourInstance.preferences.edit().clear().apply();
    }

    /**
     * Giriş ekranı olduğu durumlarda beni hatırla için kullanacağız.
     */
    public static void removeWithExFilter(String ex) {
        Map<String, ?> allEntries = ourInstance.preferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (!entry.getKey().contains(ex)) {
                ourInstance.preferences.edit().remove(entry.getKey()).apply();
                ourInstance.preferences.edit().apply();
            } else {
            }
        }
    }

}