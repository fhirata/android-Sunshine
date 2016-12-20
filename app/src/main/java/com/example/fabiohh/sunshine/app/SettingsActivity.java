package com.example.fabiohh.sunshine.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.fabiohh.sunshine.app.data.WeatherContract;
import com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import static com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter.LOCATION_STATUS_OK;
import static com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter.LOCATION_STATUS_SERVER_INVALID;
import static com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter.LOCATION_STATUS_UNKNOWN;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    static int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }


    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        setPreferenceSummary(preference, value);

        return true;
    }

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        String key = preference.getKey();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (key.equals(getString(R.string.pref_location_key))) {
            @SunshineSyncAdapter.LocationStatus int status = Utility.getLocationStatus(this);
            switch (status) {
                case LOCATION_STATUS_OK:
                    preference.setSummary(stringValue);
                    break;
                case LOCATION_STATUS_UNKNOWN:
                    preference.setSummary(getString(R.string.pref_location_unknown_description, value));
                    break;
                case LOCATION_STATUS_SERVER_INVALID:
                    preference.setSummary(getString(R.string.pref_location_error_description, value));
                    break;
                default:
                    preference.setSummary(stringValue);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(this.getString(R.string.pref_location_result))) {
            Utility.resetLocationStatus(this);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(getString(R.string.pref_lon_location));
            editor.remove(getString(R.string.pref_lat_location));
            editor.commit();

            SunshineSyncAdapter.syncImmediately(this);
        } else if (key.equals(this.getString(R.string.pref_units_key))) {
            getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
        }
    }

    @Override
    protected void onResume() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String address = place.getAddress().toString();
                LatLng latLng = place.getLatLng();

                if (TextUtils.isEmpty(address)) {
                    address = String.format("(%.2f, @.2f)", latLng.latitude, latLng.longitude);
                }

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(getString(R.string.pref_location_key), address);
                editor.putString(getString(R.string.pref_lat_location), String.valueOf(latLng.latitude));
                editor.putString(getString(R.string.pref_lon_location), String.valueOf(latLng.longitude));
                editor.commit();

                Preference locationPreference = findPreference(getString(R.string.pref_location_key));
                setPreferenceSummary(locationPreference, address);
                Utility.resetLocationStatus(this);
                SunshineSyncAdapter.syncImmediately(this);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
