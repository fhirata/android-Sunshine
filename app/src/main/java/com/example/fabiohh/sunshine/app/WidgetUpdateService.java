package com.example.fabiohh.sunshine.app;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.Time;
import android.widget.RemoteViews;

import com.example.fabiohh.sunshine.app.data.Weather;
import com.example.fabiohh.sunshine.app.data.WeatherContract;
import com.example.fabiohh.sunshine.app.widget.TodayWidgetProvider;

import static com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter.NOTIFY_WEATHER_PROJECTION;
import static java.lang.System.currentTimeMillis;

/**
 * Created by fabiohh on 12/20/16.
 */

public class WidgetUpdateService extends IntentService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TodayWidgetProvider.class));

        String locationQuery = Utility.getPreferredLocation(this);

        Time dayTime = new Time();
        dayTime.setToNow();
        int julianStartDay = Time.getJulianDay(currentTimeMillis(), dayTime.gmtoff);

        long dateTime = dayTime.setJulianDay(julianStartDay);
        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationQuery, dateTime);

        // we'll query our contentProvider, as always
        Cursor cursor = getContentResolver().query(weatherUri, NOTIFY_WEATHER_PROJECTION, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Weather weatherEntry = Weather.fromCursor(this, cursor);
            cursor.close();

            for (int appWidgetId : appWidgetIds) {
                RemoteViews views = new RemoteViews(
                        getBaseContext().getPackageName(),
                        R.layout.widget_today);

                // intent to launch app
                Intent openWidgetIntent = new Intent(getBaseContext(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, openWidgetIntent, 0);
                views.setOnClickPendingIntent(R.id.today_widget, pendingIntent);

                // set widget dynamic information
                views.setImageViewResource(R.id.widget_image, weatherEntry.getIconId());
                views.setTextViewText(R.id.widget_forecast_textview,
                        Utility.formatTemperature(this, weatherEntry.getHigh(), weatherEntry.isMetric()));

                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }
}
