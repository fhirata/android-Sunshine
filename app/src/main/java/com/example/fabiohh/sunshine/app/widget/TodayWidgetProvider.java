package com.example.fabiohh.sunshine.app.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.example.fabiohh.sunshine.app.WidgetUpdateService;
import com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter;

/**
 * Created by fabiohh on 12/20/16.
 */

public class TodayWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (SunshineSyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context, WidgetUpdateService.class));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, WidgetUpdateService.class));
    }
}
