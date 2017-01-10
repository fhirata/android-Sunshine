package com.example.fabiohh.sunshine.app.muzei;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.text.format.Time;

import com.example.fabiohh.sunshine.app.ForecastFragment;
import com.example.fabiohh.sunshine.app.MainActivity;
import com.example.fabiohh.sunshine.app.Utility;
import com.example.fabiohh.sunshine.app.data.WeatherContract;
import com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter;
import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;

import static com.example.fabiohh.sunshine.app.ForecastFragment.FORECAST_COLUMNS;

/**
 * Created by fabiohh on 12/21/16.
 */

public class WeatherMuzeiSource extends MuzeiArtSource {
    public WeatherMuzeiSource() {
        super("WeatherMuzeiSource");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        boolean dataUpdated = intent != null && SunshineSyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction());
        if (dataUpdated && isEnabled()) {
            onUpdate(UPDATE_REASON_OTHER);
        }
    }

    @Override
    protected void onUpdate(int reason) {
        String locationQuery = Utility.getPreferredLocation(this);
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        Time dayTime = new Time();
        dayTime.setToNow();
        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        long dateTime = dayTime.setJulianDay(julianStartDay);
        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationQuery, dateTime);

        // This method is called by the app hosting the widget (e.g., the launcher)
        // However, our ContentProvider is not exported so it doesn't have access to the
        // data. Therefore we need to clear (and finally restore) the calling identity so
        // that calls use our process and permission
        final long identityToken = Binder.clearCallingIdentity();
        Cursor cursor = getContentResolver().query(weatherUri, FORECAST_COLUMNS, null, null, sortOrder);
        Binder.restoreCallingIdentity(identityToken);
        if (cursor.moveToFirst()) {
            int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
            String desc = cursor.getString(ForecastFragment.COL_WEATHER_DESC);

            String imageUrl = Utility.getArtUrlForWeatherCondition(this, weatherId);

            if (null != imageUrl) {
                publishArtwork(new Artwork.Builder()
                        .imageUri(Uri.parse(imageUrl))
                .title(desc)
                .byline(locationQuery)
                .viewIntent(new Intent(this, MainActivity.class))
                .build());
            }
        }
        if (null != cursor) {
            cursor.close();
        }
    }
}
