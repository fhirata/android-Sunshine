package com.example.fabiohh.sunshine.app.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.fabiohh.sunshine.app.R;
import com.example.fabiohh.sunshine.app.sync.SunshineSyncAdapter;

/**
 * Created by fabiohh on 12/20/16.
 */

public class DetailWidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (SunshineSyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_forecast_stackview);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_detail_stackview);

            views.setRemoteAdapter(R.id.widget_forecast_stackview, new Intent(context, DetailWidgetRemoteViewsService.class));

            views.setEmptyView(R.id.widget_forecast_stackview, R.id.widget_empty_view);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
