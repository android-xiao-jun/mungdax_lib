package com.allo.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * 从下至上的跑马灯
 */
public class RollTextView extends androidx.appcompat.widget.AppCompatTextView implements Runnable {

    private int currentScrollY;// 当前滚动的位置

    private CharSequence newText;

    private int currentColor;

    private int fmTop;

    public RollTextView(Context context) {
        super(context);
    }

    public RollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void run() {
        currentScrollY += 1;// 滚动速度
        scrollTo(0, currentScrollY);
        //setTranslationY(currentScrollY);
        if (currentScrollY == 0) {
            return;
        }

        if (getScrollY() >= getHeight()) {
        //if (getTranslationY() >= getHeight()) {
            if (getText() != newText) {
                currentColor = getCurrentTextColor();
                setTextColor(Color.TRANSPARENT);
                setText(newText);
            }

            scrollTo(0, -getHeight());
            currentScrollY = -getHeight();
        }

        if (getScrollY() == fmTop) {
            setTextColor(currentColor);
        }

        postDelayed(this, 2);
    }

    // 从头开始滚动
    private void startFor0() {
        currentScrollY = 0;
        removeCallbacks(this);
        post(this);
        //new Thread(this).start();
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    public void setNewText(CharSequence lastText, CharSequence newText) {
        this.newText = newText;
        setText(lastText);
        startFor0();
        Paint.FontMetrics fm = getPaint().getFontMetrics();
        fmTop = (int) fm.top;
    }
}
