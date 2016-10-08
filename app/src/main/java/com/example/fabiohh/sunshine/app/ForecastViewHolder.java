package com.example.fabiohh.sunshine.app;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by fabiohh on 10/7/16.
 */

public class ForecastViewHolder {

    public final TextView dateTextView;
    public final TextView forecastTextView;
    public final TextView highTextView;
    public final TextView lowTextView;
    public final ImageView iconView;

    public ForecastViewHolder(View view) {
        iconView =  (ImageView)view.findViewById(R.id.list_item_icon);
        dateTextView = (TextView)view.findViewById(R.id.list_item_date_textview);
        forecastTextView = (TextView)view.findViewById(R.id.list_item_forecast_textview);
        highTextView = (TextView)view.findViewById(R.id.list_item_high_textview);
        lowTextView = (TextView)view.findViewById(R.id.list_item_low_textview);
    }
}
