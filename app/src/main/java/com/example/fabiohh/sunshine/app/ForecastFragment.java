package com.example.fabiohh.sunshine.app;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.fabiohh.sunshine.app.data.WeatherContract;

/**
 * Created by fabiohh on 8/2/16.
 */
public class ForecastFragment extends Fragment {

    private static final String LOG_TAG = Fragment.class.getName();
    private ForecastAdapter mForecastAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri,
                null, null, null, sortOrder);

        mForecastAdapter = new ForecastAdapter(getActivity(), cur, 0);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String zipcode = settings.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));

        Log.w(LOG_TAG, "zip: " + zipcode);
        weatherTask.execute(zipcode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }


//    private class FetchWeatherTask extends AsyncTask<String, Void, ArrayList<WeatherInfo>> {
//
//        @Override
//        protected ArrayList<WeatherInfo> doInBackground(String... params) {
//            // These two need to be declared outside the try/catch
//            // so that they can be closed in the finally block.
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            // Will contain the raw JSON response as a string.
//            String forecastJsonStr = null;
//
//            String units = "metric";
//            String mode = "json";
//            String cnt = "7";
//            String key = "ce528ad204bac5e4899e547f4ba62323";
//
//            final String FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
//            final String QUERY_PARAM = "q";
//            final String UNITS_PARAM = "units";
//            final String MODE_PARAM = "mode";
//            final String COUNT_PARAM = "cnt";
//            final String KEY_PARAM = "APPID";
//
//            Uri uri = Uri.parse(FORECAST_URL).buildUpon()
//                .appendQueryParameter(QUERY_PARAM, params[0])
//                    .appendQueryParameter(UNITS_PARAM, units)
//                    .appendQueryParameter(MODE_PARAM, mode)
//                    .appendQueryParameter(COUNT_PARAM, cnt)
//                    .appendQueryParameter(KEY_PARAM, key).build();
//
//            Log.v("URI", "URI is " + uri.toString());
//            try {
//                // Construct the URL for the OpenWeatherMap query
//                // Possible parameters are available at OWM's forecast API page, at
//                // http://openweathermap.org/API#forecast
//                URL url = new URL(uri.toString());
//
//                // Create the request to OpenWeatherMap, and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    forecastJsonStr = null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    forecastJsonStr = null;
//                }
//                forecastJsonStr = buffer.toString();
//
//                Log.w("Content", forecastJsonStr);
//
//            } catch (IOException e) {
//                Log.e("PlaceholderFragment", "Error ", e);
//                // If the code didn't successfully get the weather data, there's no point in attempting
//                // to parse it.
//                forecastJsonStr = null;
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e("PlaceholderFragment", "Error closing stream", e);
//                    }
//                }
//            }
//            try {
//                return getWeatherDataFromJson(forecastJsonStr, Integer.valueOf(cnt));
//            } catch (JSONException jsonException) {
//                Log.e(LOG_TAG, jsonException.getMessage(), jsonException);
//                jsonException.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<WeatherInfo> weatherInfoArrayList) {
//            mForecastAdapter.clear();
//
//            String[] stringArray = new String[weatherInfoArrayList.size()];
//            Iterator<WeatherInfo> iterator = weatherInfoArrayList.iterator();
//            int i = 0;
//
//            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
//            int temperatureUnitValue = WeatherInfo.CELCIUS;
//
//            if (settings.contains(getString(R.string.pref_units))) {
//                String preferenceValue = settings.getString(getString(R.string.pref_units), getString(R.string.pref_metric)); // default "0" -> CELCIUS
//                temperatureUnitValue = new Integer(preferenceValue).intValue();
//            }
//
//            Log.d(LOG_TAG, "type: " + temperatureUnitValue);
//
//            // TODO: Apply stream.map in the future
//            while (iterator.hasNext()) {
//                WeatherInfo weatherInfo = iterator.next();
//                stringArray[i++] = weatherInfo.getFormated(temperatureUnitValue);
//            }
//
//            mForecastAdapter.addAll(stringArray);
//
//            super.onPostExecute(weatherInfoArrayList);
//        }
//
//        private String getReadableDateString(long time) {
//            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
//            return shortenedDateFormat.format(time);
//        }
//
//
//        /**
//         * Take the String representing the complete forecast in JSON Format and
//         * pull out the data we need to construct the Strings needed for the wireframes.
//         *
//         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
//         * into an Object hierarchy for us.
//         */
//        private ArrayList<WeatherInfo> getWeatherDataFromJson(String forecastJsonStr, int numDays)
//                throws JSONException {
//
//            // These are the names of the JSON objects that need to be extracted.
//            final String OWM_LIST = "list";
//            final String OWM_WEATHER = "weather";
//            final String OWM_TEMPERATURE = "temp";
//            final String OWM_MAX = "max";
//            final String OWM_MIN = "min";
//            final String OWM_DESCRIPTION = "main";
//
//            JSONObject forecastJson = new JSONObject(forecastJsonStr);
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
//
//            // OWM returns daily forecasts based upon the local time of the city that is being
//            // asked for, which means that we need to know the GMT offset to translate this data
//            // properly.
//
//            // Since this data is also sent in-order and the first day is always the
//            // current day, we're going to take advantage of that to get a nice
//            // normalized UTC date for all of our weather.
//
//            Time dayTime = new Time();
//            dayTime.setToNow();
//
//            // we start at the day returned by local time. Otherwise this is a mess.
//            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
//
//            // now we work exclusively in UTC
//            dayTime = new Time();
//
//            ArrayList<WeatherInfo> weatherInfoResult = new ArrayList<WeatherInfo>(numDays);
//            for(int i = 0; i < weatherArray.length(); i++) {
//                // For now, using the format "Day, description, hi/low"
//                String day;
//                String description;
//                String highAndLow;
//
//                // Get the JSON object representing the day
//                JSONObject dayForecast = weatherArray.getJSONObject(i);
//
//                // The date/time is returned as a long.  We need to convert that
//                // into something human-readable, since most people won't read "1400356800" as
//                // "this saturday".
//                long dateTime;
//                // Cheating to convert this to UTC time, which is what we want anyhow
//                dateTime = dayTime.setJulianDay(julianStartDay+i);
//                day = getReadableDateString(dateTime);
//
//                // description is in a child array called "weather", which is 1 element long.
//                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWM_DESCRIPTION);
//
//                // Temperatures are in a child object called "temp".  Try not to name variables
//                // "temp" when working with temperature.  It confuses everybody.
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//
//                double high = temperatureObject.getDouble(OWM_MAX);
//                double low = temperatureObject.getDouble(OWM_MIN);
//
//                weatherInfoResult.add(new WeatherInfo(day, description, high, low));
//            }
//
////            for (String s : resultStrs) {
////                Log.v(LOG_TAG, "Forecast entry: " + s);
////            }
//            return weatherInfoResult;
//
//        }
//    }
}