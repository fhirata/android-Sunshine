package com.example.fabiohh.sunshine.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fabiohh on 10/9/16.
 */

public class TowerView extends View {
    public TowerView(Context context) {
        super(context);
    }

    public TowerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TowerView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
    }

    @Override
    @TargetApi(21)
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        float x = 140;
        float y = 80;
        int tower_width = 20;
        int tower_height = 300;

        paint.setColor(Color.GRAY);
        RectF rectf = new RectF(x, y, x + tower_width, y + tower_height);
        canvas.drawArc(rectf, 180, 90, true, paint);

        RectF rectf2 = new RectF(x, y, x + tower_width, y + tower_height);
        canvas.drawArc(rectf2, 0, -90, true, paint);

        float center_radius = 15;
        paint.setColor(Color.GRAY);
        canvas.drawOval(x+(tower_width/2)-(center_radius), y-(center_radius) + 10, x + (tower_width/2) + center_radius, y + (center_radius) + 10, paint);

    }
}
