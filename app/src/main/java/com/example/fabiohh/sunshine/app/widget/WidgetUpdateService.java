package com.example.fabiohh.sunshine.app.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.example.fabiohh.sunshine.app.MainActivity;
import com.example.fabiohh.sunshine.app.R;
import com.example.fabiohh.sunshine.app.Utility;
import com.example.fabiohh.sunshine.app.data.Weather;
import com.example.fabiohh.sunshine.app.data.WeatherContract;

import static com.example.fabiohh.sunshine.app.R.layout.widget_today;
import static com.example.fabiohh.sunshine.app.R.layout.widget_today_large;
import static com.example.fabiohh.sunshine.app.R.layout.widget_today_small;
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
                int widgetWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);

                Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
                if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
                    int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    widgetWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp, displayMetrics);
                }

                int defaultWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
                int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_large_width);

                int layoutId;
                if (widgetWidth >= largeWidth) {
                    layoutId = widget_today_large;
                } else if (widgetWidth >= defaultWidth) {
                    layoutId = widget_today;
                } else {
                    layoutId = widget_today_small;
                }
                RemoteViews views = new RemoteViews(
                        getBaseContext().getPackageName(), layoutId);

                // intent to launch app
                Intent openWidgetIntent = new Intent(getBaseContext(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, openWidgetIntent, 0);
                views.setOnClickPendingIntent(R.id.today_widget, pendingIntent);

                // set widget dynamic information
                views.setImageViewResource(R.id.widget_image, weatherEntry.getIconId());
                views.setTextViewText(R.id.widget_forecast_textview, weatherEntry.getDesc());
                views.setTextViewText(R.id.widget_high_textview,
                        Utility.formatTemperature(this, weatherEntry.getHigh(), weatherEntry.isMetric()));
                views.setTextViewText(R.id.widget_low_textview,
                        Utility.formatTemperature(this, weatherEntry.getLow(), weatherEntry.isMetric()));

                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }
}
