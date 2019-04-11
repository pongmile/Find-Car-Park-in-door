package com.pongmile.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import com.pongmile.model.Dot;


public class Dotview extends View {

    private Paint paint;
    private ArrayList<Dot> dots;
    private OnTouchListener listener;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.dots != null) {
            for (Dot dot : dots) {
                paint.setColor(dot.getColor());
                canvas.drawCircle(dot.getCenterX(), dot.getCenterY(), dot.getRadius(), paint);
            }
        }
    }

    public Dotview(Context context) {
        super(context);
        paint = new Paint();
        dots = new ArrayList<>();
    }

    public Dotview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        dots = new ArrayList<>();
    }

    public Dotview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        dots = new ArrayList<>();
    }

    public Dotview(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        dots = new ArrayList<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        listener.onTouch(this, event);

        return super.onTouchEvent(event);
    }

    public void addDot(Dot dot) {
        dots.add(dot);
    }

    public void clear() {
        dots.clear();
    }

    public void setOnTouchListener(OnTouchListener listener) {
        this.listener = listener;
    }
}
