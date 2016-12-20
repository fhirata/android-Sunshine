package com.example.fabiohh.sunshine.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fabiohh.sunshine.app.data.WeatherContract;
import com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter;

import static com.example.fabiohh.sunshine.app.R.id.textview_no_weather_info;
import static com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter.LOCATION_STATUS_SERVER_DOWN;
import static com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter.LOCATION_STATUS_SERVER_INVALID;
import static com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter.LOCATION_STATUS_SERVER_INVALID_LOCATION;

/**
 * Created by fabiohh on 8/2/16.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = Fragment.class.getName();

    private ForecastAdapter mForecastAdapter;
    private String SELECTED_KEY = "position";
    public String ZIPCODE_VALUE = "zip";

    private static final int FORECAST_LOADER = 1;
    private boolean mUseTodayLayout;
    RecyclerView mRecyclerView;
    View mEmptyView;
    int mPosition = ListView.INVALID_POSITION;
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG,
            WeatherContract.LocationEntry.COLUMN_CITY_NAME
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;
    static final int COL_CITY_NAME = 9;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mEmptyView = rootView.findViewById(R.id.textview_no_weather_info);

        // Get a reference to the RecyclerView, and attach this adapter to it.
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listview_forecast);

        mRecyclerView.setHasFixedSize(true);

        // The ForecastAdapter will take data from a source and
        // use it to populate the RecyclerView it's attached to.
        mForecastAdapter = new ForecastAdapter(getActivity(), new ForecastAdapter.ForecastAdapterOnClickHandler() {
            @Override
            public void onClick(Long date, ForecastAdapter.ForecastAdapterViewHolder vh) {
                                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    ((Callback) getActivity())
                            .onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, date));

                mPosition = vh.getAdapterPosition();
            }
        }, mEmptyView);

        // Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View emptyView = rootView.findViewById(textview_no_weather_info);
        mRecyclerView.setAdapter(mForecastAdapter);
        // We'll call our MainActivity

        final View parallaxView = rootView.findViewById(R.id.parallax_bar);
        if (null != parallaxView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        int max = parallaxView.getHeight();
                        if (dy > 0) {
                            parallaxView.setTranslationY(Math.max(-max, parallaxView.getTranslationY() - dy / 2));
                        } else {
                            parallaxView.setTranslationY(Math.min(0, parallaxView.getTranslationY() - dy / 2));
                        }

                    }
                });
            }
        }

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The RecyclerView probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mRecyclerView) {
            mRecyclerView.clearOnScrollListeners();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    private void updateWeather() {
        com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    void onLocationChanged() {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;

            case R.id.action_map: {
                openPreferredLocationInMap();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        String zipcode = "";

        Cursor c = mForecastAdapter.getCursor();
        if (null != c) {
            c.moveToFirst();
            String lat = c.getString(COL_COORD_LAT);
            String lon = c.getString(COL_COORD_LONG);
            Uri geoLocation = Uri.parse("geo:" + lat + "," + lon);

            Intent mapIntent = new Intent(Intent.ACTION_VIEW);
            mapIntent.setData(geoLocation);

            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        Time dayTime = new Time();
        dayTime.setToNow();
        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        long dateTime = dayTime.setJulianDay(julianStartDay);


        String locationSetting = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, dateTime);

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.smoothScrollToPosition(mPosition);
        }

        updateEmptyView();
    }

    private void updateEmptyView() {
        if (mForecastAdapter.getItemCount() != 0) {
            TextView noWeatherTextview = (TextView) getActivity().findViewById(textview_no_weather_info);
            if (null != noWeatherTextview) {
                int message = R.string.empty_forecast_list;
                @SunshineSyncAdapter.LocationStatus int location = Utility.getLocationStatus(this.getActivity());
                switch (location) {
                    case LOCATION_STATUS_SERVER_DOWN:
                        message = R.string.empty_forecast_list_server_down;
                        break;
                    case LOCATION_STATUS_SERVER_INVALID:
                        message = R.string.empty_forecast_list_server_error;
                        break;
                    case LOCATION_STATUS_SERVER_INVALID_LOCATION:
                        message = R.string.empty_forecast_list_invalid_location;

                    default:
                        if (!Utility.isNetworkAvailable(this.getActivity())) {
                            message = R.string.empty_forecast_list_no_network;
                        }
                }
                noWeatherTextview.setText(message);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mForecastAdapter != null) {
            mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_location_result))) {
            updateEmptyView();
        }
        mForecastAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }
}
