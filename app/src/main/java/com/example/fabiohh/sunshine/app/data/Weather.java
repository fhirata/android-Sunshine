package com.example.fabiohh.sunshine.app.data;

import android.content.Context;
import android.database.Cursor;

import com.example.fabiohh.sunshine.app.Utility;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

import static com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter.INDEX_MAX_TEMP;
import static com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter.INDEX_MIN_TEMP;
import static com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter.INDEX_SHORT_DESC;
import static com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter.INDEX_WEATHER_ID;

/**
 * Created by fabiohh on 9/3/16.
 */
public class Weather {

    private int mWeatherId;
    private double mHigh;
    private double mLow;
    private String mDesc;
    private boolean mIsMetric;
    private int mIconId;


    public int getWeatherId() {
        return mWeatherId;
    }

    public void setWeatherId(int mWeatherId) {
        this.mWeatherId = mWeatherId;
    }

    public double getHigh() {
        return mHigh;
    }

    public void setHigh(double mHigh) {
        this.mHigh = mHigh;
    }

    public double getLow() {
        return mLow;
    }

    public void setLow(double mLow) {
        this.mLow = mLow;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public boolean isMetric() {
        return mIsMetric;
    }

    public void setIsMetric(boolean mIsMetric) {
        this.mIsMetric = mIsMetric;
    }

    public int getIconId() {
        return mIconId;
    }

    public void setIconId(int mIconId) {
        this.mIconId = mIconId;
    }


    public static Weather fromCursor(Context context, Cursor cursor) {

        Weather weather = new Weather();

        weather.setWeatherId(cursor.getInt(INDEX_WEATHER_ID));
        weather.setHigh(cursor.getDouble(INDEX_MAX_TEMP));
        weather.setLow(cursor.getDouble(INDEX_MIN_TEMP));
        weather.setDesc(cursor.getString(INDEX_SHORT_DESC));
        weather.setIsMetric(Utility.isMetric(context));

        weather.setIconId(Utility.getIconResourceForWeatherCondition(weather.getWeatherId()));

        return weather;
    }

    public static Weather fromRemoteData(RemoteMessage remoteMessage) {
        Weather weather = new Weather();

        HashMap<String, String> map = new HashMap<>(remoteMessage.getData());

        if (map.containsKey("desc")) {
            weather.setDesc(map.get("desc"));
        }

        if (map.containsKey("high")) {
            weather.setHigh(Double.valueOf(map.get("high")));
        }

        if (map.containsKey("low")) {
            weather.setLow(Double.valueOf(map.get("low")));
        }

        if (map.containsKey("weather_id")) {
            weather.setWeatherId(Integer.valueOf(map.get("weather_id")));
        }

        if (map.containsKey("icon_id")) {
            weather.setIconId(Integer.valueOf(map.get("icon_id")));
        }

        return weather;
    }
}