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
 * Created by fabiohh on 10/15/16.
 */

public class WindmillView extends View {

    public WindmillView(Context context) {
        super(context);

    }

    public WindmillView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public WindmillView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);

    }

    @Override
    @TargetApi(21)
    protected void onDraw(Canvas canvas) {

        int width = 195;
        int height = 15;

        int x = canvas.getWidth()/2;
        int y = canvas.getHeight()/2;

        Paint paint = new Paint();

        paint.setColor(Color.GRAY);
        RectF rectf = new RectF(x - (width/2), y-(height/2), x + width/2, y + height/2);
        canvas.drawArc(rectf, 0, 90, true, paint);

        paint.setColor(Color.GRAY);
        paint.setAlpha(200);
        RectF rectf2 = new RectF(x - (width/2), y-(height/2), x + width/2, y + height/2);
        canvas.drawArc(rectf2, 0, -90, true, paint);

    }
}
