/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.fabiohh.sunshine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private ForecastAdapter mForecastAdapter;
    private final Context mContext;

    public FetchWeatherTask(Context context) {
        mContext = context;
    }

    private boolean DEBUG = true;






    @Override
    protected String[] doInBackground(String... params) {

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }
//
//    @Override
//    protected void onPostExecute(String[] result) {
//        if (result != null && mForecastAdapter != null) {
//            mForecastAdapter.clear();
//            for(String dayForecastStr : result) {
//                mForecastAdapter.add(dayForecastStr);
//            }
//            // New data is back from the server.  Hooray!
//        }
//    }
}