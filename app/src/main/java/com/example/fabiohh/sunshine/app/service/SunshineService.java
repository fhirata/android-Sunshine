package com.example.fabiohh.sunshine.app.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by fabiohh on 10/24/16.
 */

public class SunshineService extends IntentService {
    public String LOG_TAG = SunshineService.class.getSimpleName();

    public SunshineService() {
        super("Sunshine Service");
    }

    public SunshineService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }



    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String zipcode = intent.getStringExtra(SunshineService.LOCATION_SERVICE);
            Intent sendIntent = new Intent(context, SunshineService.class);
            sendIntent.putExtra(SunshineService.LOCATION_SERVICE, zipcode);
            context.startService(sendIntent);
        }
    }
}
