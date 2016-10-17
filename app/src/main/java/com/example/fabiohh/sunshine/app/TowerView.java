package com.example.fabiohh.sunshine.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fabiohh on 10/9/16.
 */

public class TowerView extends View {
    float center_radius = 10;
    int tower_width = 30;
    int tower_height = 300;
    float x;
    float y;
    RadialGradient gradient;
    Paint paint;
    RectF rectf1;
    RectF rectf2;

    private void init() {

    }

    public TowerView(Context context) {
        super(context);

        init();
    }

    public TowerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public TowerView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        init();
    }

    @Override
    @TargetApi(21)
    protected void onDraw(Canvas canvas) {
        x = canvas.getWidth()/2;
        y = canvas.getHeight()/2;

        rectf1 = rectf2 = new RectF(x-tower_width/2, y, x + tower_width/2, y + tower_height);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);

        // Draw tower arches
        canvas.drawArc(rectf1, 180, 90, true, paint);
        canvas.drawArc(rectf2, 0, -90, true, paint);

        // Draw Circle
        gradient = new RadialGradient(x, y, center_radius, Color.WHITE,
                Color.GRAY, android.graphics.Shader.TileMode.CLAMP);
        paint.setDither(true);
        paint.setShader(gradient);

        canvas.drawCircle(x, y, center_radius, paint);
    }
}
