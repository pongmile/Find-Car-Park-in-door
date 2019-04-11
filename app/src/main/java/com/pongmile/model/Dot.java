package com.pongmile.model;

public class Dot {

    public interface OnDotChangedListener {
        void onDotChanged(Dot dot);
    }

    private OnDotChangedListener listener;
    private int centerX;
    private int centerY;
    private int radius;
    private int color;

    public Dot(int centerX, int centerY, int radius, int color, OnDotChangedListener listener) {
        this.listener = listener;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.color = color;

        this.listener.onDotChanged(this);
    }

    public Dot(int centerX, int centerY, int radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public void setListener(OnDotChangedListener listener) {
        this.listener = listener;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
        this.listener.onDotChanged(this);
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
        this.listener.onDotChanged(this);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
