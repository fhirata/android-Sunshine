package com.example.fabiohh.sunshine.app;

/**
 * Created by fabiohh on 8/11/16.
 */
public class WeatherInfo {
    double high;
    double low;
    String day;
    String description;

    public static final int CELCIUS    = 0;
    public static final int FAHRENHEIT = 1;

    public WeatherInfo(String day, String description, double high, double low) {
        this.high = high;
        this.low = low;

        this.description = description;
        this.day = day;
    }

    public String getFormated(int temperatureUnit) {
        double high = this.high;
        double low = this.low;

        if (temperatureUnit == FAHRENHEIT) {
            high = convertToFarenheit(high);
            low  = convertToFarenheit(low);
        }

        formatHighLows(high, low);

        return day + " - " + description + " - " + formatHighLows(high, low);
    }

    private String formatHighLows(double high, double low) {
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        return roundedHigh + "/" + roundedLow;
    }

    protected double convertToFarenheit(double temperature) {
        return temperature * 9/5 + 32;
    }
}
