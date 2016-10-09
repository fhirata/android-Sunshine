package com.example.fabiohh.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fabiohh.sunshine.app.data.WeatherContract;

/**
 * Created by fabiohh on 10/7/16.
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ShareActionProvider mShareActionProvider;
    private String mForecastStr;
    private String mHashtag = "#SunshineApp";
    public final String TAG = DetailFragment.class.getSimpleName();
    Uri mUri;
    static final String DETAIL_URI = "URI";

    private static final int DETAIL_LOADER = 1;

    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        } else {
            Log.d(TAG, "Share Action Provider is null?");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    private Intent createShareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + mHashtag);
        sendIntent.setType("text/plain");

        return sendIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri == null) {
            return null;
        }
        return new CursorLoader(
                getActivity(),
                mUri,
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String dateString = Utility.formatDate(
                data.getLong(COL_WEATHER_DATE));

        String weatherDesc = data.getString(COL_WEATHER_DESC);
        int weatherCode = data.getInt(COL_WEATHER_CONDITION_ID);

        boolean isMetric = Utility.isMetric(getActivity());

        String humidityString = data.getString(COL_WEATHER_HUMIDITY);
        String pressureString = data.getString(COL_WEATHER_PRESSURE);
        double windDouble = data.getDouble(COL_WEATHER_WIND_SPEED);

        TextView highTextView = (TextView)getView().findViewById(R.id.list_item_high_textview);
        highTextView.setText(Utility.formatTemperature(this.getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric));

        TextView lowTextView = (TextView)getView().findViewById(R.id.list_item_low_textview);
        lowTextView.setText(Utility.formatTemperature(this.getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric));

        TextView dateTextView = (TextView)getView().findViewById(R.id.list_item_date_textview);
        dateTextView.setText(dateString);

        ImageView iconImageView = (ImageView)getView().findViewById(R.id.list_item_icon);
        iconImageView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherCode));

        TextView weatherTextView = (TextView)getView().findViewById(R.id.list_item_forecast_textview);
        weatherTextView.setText(weatherDesc);

        TextView humidityTextView = (TextView)getView().findViewById((R.id.detail_humidity_textview));
        humidityTextView.setText(humidityString);

        TextView windTextView = (TextView)getView().findViewById(R.id.detail_wind_textview);
        windTextView.setText(Utility.formatWindSpeed(this.getActivity(), windDouble, isMetric));

        TextView pressureTextView = (TextView)getView().findViewById(R.id.detail_pressure_textview);
        pressureTextView.setText(pressureString);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }
}