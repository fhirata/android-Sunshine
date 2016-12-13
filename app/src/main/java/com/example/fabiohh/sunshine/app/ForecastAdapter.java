package com.example.fabiohh.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import static com.example.fabiohh.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_DATE;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {
    final private int VIEW_TYPE_TODAY = 0;
    final private int VIEW_TYPE_FUTURE_DAY = 1;
    private Context mContext;
    private Cursor mCursor;
    final private View mEmptyView;
    final private ForecastAdapterOnClickHandler mClickHandler;

    private boolean mUseTodayLayout = true;

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(mContext, high, isMetric) + "/" + Utility.formatTemperature(mContext, low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewGroup instanceof RecyclerView) {
            int layoutId = -1;

            if (viewType == VIEW_TYPE_TODAY) {
                layoutId = R.layout.list_item_forecast_today;
            } else {
                layoutId = R.layout.list_item_forecast;
            }

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);

            return new ForecastAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerViewSelection");
        }
    }


    public Cursor getCursor() {
        return mCursor;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        if (null != mEmptyView) {
            mEmptyView.setVisibility((getItemCount() == 0) ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);
        int weatherId = mCursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);

        long dateLong = mCursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        boolean isMetric = Utility.isMetric(mContext);

        double high = mCursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        double low = mCursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        int defaultImage;

        switch (getItemViewType(position)) {
            case VIEW_TYPE_TODAY:
                defaultImage = Utility.getArtResourceForWeatherCondition(weatherId);
                break;
            default:
                defaultImage = Utility.getIconResourceForWeatherCondition(weatherId);
        }

        if (Utility.usingLocalGraphics(mContext)) {
            viewHolder.iconView.setImageResource(defaultImage);
        } else {
            Glide.with(mContext)
                    .load(Utility.getArtUrlForWeatherCondition(mContext, weatherId))
                    .error(defaultImage)
                    .crossFade()
                    .into(viewHolder.iconView);
        }
        String weatherDescription = mCursor.getString(ForecastFragment.COL_WEATHER_DESC);
        viewHolder.iconView.setContentDescription(weatherDescription);

        viewHolder.dateTextView.setText(Utility.getFriendlyDayString(mContext, dateLong));
        viewHolder.highTextView.setText(Utility.formatTemperature(mContext, high, isMetric));
        viewHolder.lowTextView.setText(Utility.formatTemperature(mContext, low, isMetric));
        viewHolder.forecastTextView.setText(weatherDescription);
        mCursor.getString(ForecastFragment.COL_WEATHER_CONDITION_ID);
    }

    @Override
    public int getItemViewType(int position) {
//        return (position == 0) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView dateTextView;
        public final TextView forecastTextView;
        public final TextView highTextView;
        public final TextView lowTextView;
        public final ImageView iconView;

        public ForecastAdapterViewHolder(View view) {
            super(view);
            iconView = (ImageView) view.findViewById(R.id.detail_icon);
            dateTextView = (TextView) view.findViewById(R.id.detail_date_textview);
            forecastTextView = (TextView) view.findViewById(R.id.detail_forecast_textview);
            highTextView = (TextView) view.findViewById(R.id.detail_high_textview);
            lowTextView = (TextView) view.findViewById(R.id.detail_low_textview);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int dateColumnIndex = mCursor.getColumnIndex(COLUMN_DATE);
            mClickHandler.onClick(mCursor.getLong(dateColumnIndex), this);
        }
    }

    public static interface ForecastAdapterOnClickHandler {
        void onClick(Long date, ForecastAdapterViewHolder vh);

    }

    public ForecastAdapter(Context context, ForecastAdapterOnClickHandler dh, View emptyView) {
        mContext = context;
        mEmptyView = emptyView;
        mClickHandler = dh;
    }
}