package com.allo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.allo.utils.RegexUtils;
import com.allo.utils.SizeUtils;
import com.allo.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AlphaIndexSideBar extends View {
    // 触摸事件
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private InTouchingListener inTouchingListener;

    public static final List<String> CHINESE_INDEX_BAR = StringUtils.INSTANCE.chineseLetters();

    public static final List<String> UY_INDEX_BAR = StringUtils.INSTANCE.uyLetters();

    // current chosen letter position
    private int mCurrentTouchPosition = -1;

    //custom filed
    //TODO declare attr
    private Paint mPaint;
    private Paint mBubblePaint;
    private Paint mBackgroundPaint;
    private List<String> mAlpha;
    private int mAlphabetHeight;
    private int mTextSize;
    private int touchArea;
    private int mNormalTextColor;
    private int mSelectedTextColor;
    private int bubbleMargin;
    private int bubbleSize;
    private int bubbleBgColor;
    private int backgroundColor;
    private boolean isSingleHeight = false;
    private final Rect bubbleTextBound = new Rect();
    private Bitmap mBubbleBitmap;

    //inner filed
    private float singleHeight;
    private float offset;
    private int textContentWidth;//文字绘制空间的大小
    private Typeface typeface;
    private boolean isDrawBubble = false;
    private int bridgeHeight;
    private int bridgeOffset;

    public AlphaIndexSideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AlphaIndexSideBar(Context context) {
        this(context, null);
    }

    public AlphaIndexSideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void init() {
        mAlpha = new ArrayList<>();
        mPaint = new Paint();
        mBubblePaint = new Paint();
        mBackgroundPaint = new Paint();
        mBubblePaint.setAntiAlias(true);
        mBubblePaint.setStyle(Paint.Style.FILL);
        mBubblePaint.setTextAlign(Paint.Align.CENTER);
        mBubblePaint.setTextSize(SizeUtils.Companion.dp2px(20));
        mBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.search_bg));
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mTextSize = SizeUtils.Companion.dp2px(10);
        touchArea = SizeUtils.Companion.dp2px(40);
        bubbleMargin = SizeUtils.Companion.dp2px(22);
        bubbleSize = SizeUtils.Companion.dp2px(20);
        mNormalTextColor = ContextCompat.getColor(getContext(), R.color.color_9B_69);
        bubbleBgColor = ContextCompat.getColor(getContext(), R.color.search_bg);
        mSelectedTextColor = ContextCompat.getColor(getContext(), R.color.text_purple);
        mBubbleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.shape_top_circle_white_solid);
        bridgeHeight = SizeUtils.Companion.dp2px(4f);
        bridgeOffset = SizeUtils.Companion.dp2px(2f);

        BitmapFactory.Options options = new BitmapFactory.Options();
        TypedValue value = new TypedValue();
        options.inTargetDensity = value.density;
        options.inScaled = false;
        mBubbleBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bg_letter, options);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        calculateTextContent();
        int width = textContentWidth + getPaddingRight() + getPaddingLeft() + bubbleMargin + bubbleSize * 2;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, getMeasuredHeight());
        // 获取对应高度
        mAlphabetHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        // 获取每一个字母的高度
        if (!isSingleHeight) {
            singleHeight = mAlphabetHeight * 1.0f / (isEmptyContent() ? 1 : mAlpha.size());
        }

        offset = (getMeasuredHeight() - singleHeight * mAlpha.size()) / 2F;
    }


    /**
     * 计算文字最大的长度
     */
    private void calculateTextContent() {
        textContentWidth = 0;
        if (isEmptyContent()) return;
        setAlphaPaint(true);
        for (String content : mAlpha) {
            textContentWidth = (int) Math.max(textContentWidth, mPaint.measureText(content));
        }
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public void setSingleHeight(int height) {
        isSingleHeight = true;
        this.singleHeight = height;
        invalidate();
    }

    private boolean isEmptyContent() {
        return mAlpha == null || mAlpha.size() == 0;
    }

    private void setAlphaPaint(boolean selected) {
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(selected ? mSelectedTextColor : mNormalTextColor);
        mPaint.setFakeBoldText(selected);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isEmptyContent()) return;


        int screenWidth = SizeUtils.Companion.getScreenWidth();
        // 控件位置 是在屏幕左方还是有方
        boolean isRight = getX() > (screenWidth >> 1);


        float left, right, bottom, top;
        left = isRight ? getWidth() - (getPaddingRight() << 1) - textContentWidth : 0;
        right = isRight ? getWidth() : textContentWidth + (getPaddingLeft() << 1);
        top = 0f;
        bottom = getHeight();

        canvas.drawRoundRect(left, top, right, bottom, 1000f, 1000f, mBackgroundPaint);

        for (int i = 0; i < mAlpha.size(); i++) {
            setAlphaPaint(i == mCurrentTouchPosition);
            String content = mAlpha.get(i);
            // x 坐标居右
            float xPos = isRight ? getWidth() - getPaddingRight() - (textContentWidth >> 1) : getPaddingLeft() + (textContentWidth >> 1);
            float yPos = offset + singleHeight * i + singleHeight / 2F
                    + mPaint.getFontMetrics().descent / 2F + mPaint.getFontMetrics().ascent / 2F;

            if (RegexUtils.uyghurFirst(content)) {
                mPaint.setTypeface(typeface);
            }
            canvas.drawText(content, xPos, yPos, mPaint);

            //draw bubble
            if (i == mCurrentTouchPosition && isDrawBubble) {
                mBubblePaint.setColor(bubbleBgColor);
                float cx = isRight ? bubbleSize + 1 : getWidth() - bubbleSize - 1;//+1防止边缘圆看不到
                float cy = yPos - getFontCenter(mPaint.getFontMetrics());
                float bLeft = isRight ? cx + bubbleSize - bridgeOffset : right;
                float bRight = isRight ? left : cx - bubbleSize + bridgeOffset;
                canvas.drawCircle(cx, cy, bubbleSize, mBubblePaint);
                canvas.drawRect(bLeft, cy + (bridgeHeight >> 1), bRight, cy - (bridgeHeight >> 1), mBubblePaint);
                mBubblePaint.setColor(ContextCompat.getColor(getContext(), R.color.bubble_text_color));
                mBubblePaint.getTextBounds(content, 0, content.length(), bubbleTextBound);
                mBubblePaint.setTypeface(RegexUtils.uyghurFirst(content) ? typeface : null);
                canvas.drawText(content, cx, cy + getFontCenter(mBubblePaint.getFontMetrics()), mBubblePaint);
            }
        }
    }

    private float getFontCenter(Paint.FontMetrics fontMetrics) {
        return (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
    }

    public void update(Collection<String> alphabet) {
        mAlpha.clear();
        mAlpha.addAll(alphabet);
        requestLayout();
    }

    private void touchCancel() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        // 重置选中
//        mCurrentTouchPosition = -1;
        isDrawBubble = false;
        invalidate();
        if (inTouchingListener != null) {
            inTouchingListener.touchStateUpdate(false);
        }
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {

        int screenWidth = SizeUtils.Companion.getScreenWidth();
        boolean isRight = getX() > (screenWidth >> 1);


        if (isRight ? event.getX() < getWidth() - touchArea : event.getX() > touchArea) {
            touchCancel();
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                touchCancel();
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isDrawBubble = true;
                final int preChoose = mCurrentTouchPosition;
                // 点击 y 坐标所占总高度的比例 * b数组的长度就等于点击 b 中的个数.
                final int size = mAlpha.size();
                if (size <= 0) {
                    return true;
                }
                final float offset = (getHeight() - singleHeight * size) / 2F;
                final int newChoosePosition = (int) (Math.ceil((event.getY() - offset) / singleHeight));
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (preChoose != newChoosePosition &&
                        newChoosePosition >= 0 && newChoosePosition < size) {
                    if (onTouchingLetterChangedListener != null) {
                        onTouchingLetterChangedListener.onChooseLetterChanged(mAlpha.get(newChoosePosition));
                    }
                    mCurrentTouchPosition = newChoosePosition;
                    invalidate();
                }
                if (inTouchingListener != null) {
                    inTouchingListener.touchStateUpdate(true);
                }
                break;
        }
        return true;
    }

    public void setSelectedIndex(String key, boolean isUy) {
        mCurrentTouchPosition = isUy ? UY_INDEX_BAR.indexOf(key) : CHINESE_INDEX_BAR.indexOf(key);
        invalidate();
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public void setInTouchingListener(InTouchingListener inTouchingListener) {
        this.inTouchingListener = inTouchingListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onChooseLetterChanged(String s);
    }

    public interface InTouchingListener {
        void touchStateUpdate(boolean isInTouching);
    }
}

