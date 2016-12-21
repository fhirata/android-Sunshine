package com.example.fabiohh.sunshine.app.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.text.format.Time;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.fabiohh.sunshine.app.ForecastFragment;
import com.example.fabiohh.sunshine.app.R;
import com.example.fabiohh.sunshine.app.Utility;
import com.example.fabiohh.sunshine.app.data.WeatherContract;

import static com.example.fabiohh.sunshine.app.ForecastFragment.FORECAST_COLUMNS;
import static com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter.INDEX_WEATHER_ID;

/**
 * Created by fabiohh on 12/20/16.
 */

public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DetailWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class DetailWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    private int mAppWidgetId;
    private Cursor mCursor;

    public DetailWidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        Time dayTime = new Time();
        dayTime.setToNow();
        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        long dateTime = dayTime.setJulianDay(julianStartDay);

        // This method is called by the app hosting the widget (e.g., the launcher)
        // However, our ContentProvider is not exported so it doesn't have access to the
        // data. Therefore we need to clear (and finally restore) the calling identity so
        // that calls use our process and permission
        final long identityToken = Binder.clearCallingIdentity();

        String locationSetting = Utility.getPreferredLocation(mContext);
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, dateTime);
        mCursor = mContext.getContentResolver().query(weatherForLocationUri, FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (null != mCursor) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        mCursor.moveToPosition(position);

        int weatherId = mCursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);

        long dateLong = mCursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        boolean isMetric = Utility.isMetric(mContext);

        double high = mCursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        double low = mCursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        String weatherDescription = mCursor.getString(ForecastFragment.COL_WEATHER_DESC);

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_detail_stack_item);
        remoteViews.setImageViewResource(R.id.detail_icon, Utility.getArtResourceForWeatherCondition(weatherId));
        remoteViews.setTextViewText(R.id.detail_date_textview, Utility.getFriendlyDayString(mContext, dateLong));
        remoteViews.setTextViewText(R.id.detail_forecast_textview, weatherDescription);
        remoteViews.setTextViewText(R.id.detail_high_textview, Utility.formatTemperature(mContext, high, isMetric));
        remoteViews.setTextViewText(R.id.detail_low_textview, Utility.formatTemperature(mContext, low, isMetric));

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_detail_stack_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (mCursor.moveToPosition(position)) {
            return mCursor.getLong(INDEX_WEATHER_ID);
        }
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
