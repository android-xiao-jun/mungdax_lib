package com.allo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;


/**
 * Created by yforyoung on 2020/03/03
 * <p>
 * 更新apk 下载进度条
 */

@SuppressLint("AppCompatCustomView")
public class GradientSeekBar extends SeekBar {
    private GradientSeekBar seekBar;
    private ViewTreeObserver observer;
    private int tDWidth, tDHeight;//thumb 宽高
    private Paint paint, paintProgress, paintText;//背景，进度
    private Path path, progressPath;//背景，进度
    private Drawable thumbDrawable, bacProgressDrawable, progressDrawable;//thumb，背景 当前进度 图片
    private int startColor, endColor;
    private int thumbScrollX = 0;//thumbu在x州滚动距离
    private int width, height;//seekbar宽高
    private float roundRadius = 20.0f; //进度条圆角半径
    private float[] radiusArray, radiusArrayProgress;//进度条圆角数组
    private int bacProgressHeight;//进度条宽高
    private final RectF progressRectF = new RectF();
    private final String bacProgressColor = "#f3f3f3";//默认背景条颜色
    private int maxProgress = 100;
    private int currentProgress = 0;
    private int lastProgress = 0;
    private int maxHeight;
    private LinearGradient mLinearGradient;
    private int textHeight = 0;
    private int textColor;
    private int thumbOffsetY = 0;
    // 是否显示进度文字
    private boolean progressText = false;
    // 进度和背景的padding
    private int progressPadding = 0;

    public GradientSeekBar(Context context) {
        super(context);
        init(null);
    }

    public GradientSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GradientSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.GradientSeekBar);
        maxHeight = typedArray.getDimensionPixelSize(R.styleable.GradientSeekBar_android_maxHeight, 0);
        roundRadius = typedArray.getDimensionPixelSize(R.styleable.GradientSeekBar_roundRadius, 0);
        tDWidth = typedArray.getDimensionPixelSize(R.styleable.GradientSeekBar_thumbWidth, 0);
        tDHeight = typedArray.getDimensionPixelSize(R.styleable.GradientSeekBar_thumbHeight, 0);
        startColor = typedArray.getColor(R.styleable.GradientSeekBar_startColor, Color.BLUE);
        endColor = typedArray.getColor(R.styleable.GradientSeekBar_endColor, Color.BLUE);
        textHeight = typedArray.getDimensionPixelSize(R.styleable.GradientSeekBar_progressTextSize, 0);
        textColor = typedArray.getColor(R.styleable.GradientSeekBar_progressTextColor, Color.BLACK);
        thumbOffsetY = typedArray.getDimensionPixelSize(R.styleable.GradientSeekBar_thumbOffsetY, 0);
        progressText = typedArray.getBoolean(R.styleable.GradientSeekBar_progressText, false);
        progressPadding = typedArray.getDimensionPixelSize(R.styleable.GradientSeekBar_progressPadding, 0);

        typedArray.recycle();
        if (thumbDrawable != null) {
            tDWidth = tDWidth == 0 ? thumbDrawable.getMinimumWidth() : tDWidth;
            tDHeight = tDHeight == 0 ? thumbDrawable.getMinimumHeight() : tDHeight;
        }
        seekBar = this;
        observer = this.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                seekBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                width = seekBar.getMeasuredWidth();
                height = seekBar.getMeasuredHeight();
            }
        });
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor(bacProgressColor));
        paintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setTextSize(textHeight);
        paintText.setColor(textColor);


        path = new Path();
        progressPath = new Path();

        radiusArray = new float[]{roundRadius, roundRadius, roundRadius, roundRadius, roundRadius, roundRadius, roundRadius, roundRadius};

        radiusArrayProgress = new float[]{roundRadius, roundRadius, 0, 0, 0, 0, roundRadius, roundRadius};

        mLinearGradient = new LinearGradient(0, 0, thumbScrollX, 0,
                startColor, endColor,
                Shader.TileMode.CLAMP);

    }


    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        bacProgressHeight = maxHeight == 0 ? tDHeight / 2 : maxHeight;
        int width = getMeasuredWidth() < getMinimumWidth() ? getMinimumWidth() : getMeasuredWidth();
        int height = getMeasuredHeight() < getMinimumHeight() ? getMinimumHeight() : getMeasuredHeight();
        if (height < tDHeight) {
            height = tDHeight;
        }
        if (maxHeight > height) {
            bacProgressHeight = height;
        }
        Log.i("Custom", "onMeasure: " + tDHeight + "   " + bacProgressHeight);
        setMeasuredDimension(width, height + textHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void setMinimumHeight(int minHeight) {
        super.setMinimumHeight(minHeight);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // background
        canvas.save();
        int pathTop = height / 2 - bacProgressHeight / 2 + textHeight - 2 * thumbOffsetY;
        int pathBottom = height / 2 + bacProgressHeight / 2 + textHeight - 2 * thumbOffsetY;
        path.addRoundRect(new RectF(tDWidth / 2f,
                        pathTop,
                        width - tDWidth / 2f,
                        pathBottom),
                radiusArray,
                Path.Direction.CW);
        if (bacProgressDrawable == null) {
            canvas.drawPath(path, paint);
        } else {
            canvas.clipPath(path);
            bacProgressDrawable.setBounds(tDWidth / 2, pathTop, width - tDWidth / 2, pathBottom);
            bacProgressDrawable.draw(canvas);
        }
        canvas.restore();

        // progress
        canvas.save();
        progressPath.reset();
        progressRectF.set(tDWidth / 2f,
                pathTop,
                thumbScrollX + tDHeight / 2f,
                pathBottom);
        progressPath.addRoundRect(progressRectF, radiusArrayProgress, Path.Direction.CW);

        if (progressDrawable != null) {
            canvas.clipPath(progressPath);
            progressDrawable.setBounds(tDWidth / 2 + progressPadding,
                    pathTop + progressPadding,
                    thumbScrollX + tDHeight / 2 - 2 * progressPadding,
                    pathBottom - progressPadding);
            progressDrawable.draw(canvas);
        } else {
            paintProgress.setShader(mLinearGradient);
            canvas.drawPath(progressPath, paintProgress);
        }
        canvas.restore();

        canvas.save();
        if (thumbDrawable != null) {
            //thumb
            thumbDrawable.setBounds(thumbScrollX,
                    height / 2 - tDHeight / 2 + textHeight - thumbOffsetY,
                    tDWidth + thumbScrollX,
                    height / 2 + tDHeight / 2 + textHeight - thumbOffsetY);
            thumbDrawable.draw(canvas);
        }
        canvas.restore();

        if (progressText) {
            canvas.save();
            float textWidth = paintText.measureText(currentProgress + "%");
            float x = tDWidth / 2 - textWidth / 2 + thumbScrollX;
            /*if (currentProgress == getMax()) {
                x -= paintText.measureText("%")/2;  //单做文字居中就这样
            }*/
            canvas.drawText(currentProgress + "%", x, textHeight, paintText);
            canvas.restore();
        }

    }

    @Override
    public synchronized void setProgress(int progress) {
        maxProgress = getMax() == 0 ? 100 : getMax();
        currentProgress = progress;
        if (maxProgress == 0 || progress > maxProgress) {
            return;
        }
        final int progressN = progress + 1;
        observer = getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                seekBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                width = seekBar.getMeasuredWidth();
                thumbScrollX = ((width - tDWidth) * progressN) / maxProgress;
                invalidate();
                calculateProgress(thumbScrollX);
            }
        });
        if (seekBar != null) {
            width = seekBar.getMeasuredWidth();
            thumbScrollX = ((width - tDWidth) * progressN) / maxProgress;

            invalidate();
            calculateProgress(thumbScrollX);
        }
    }

    @Override
    public synchronized int getProgress() {
        return this.currentProgress;
    }

    @Override
    public void setThumbOffset(int thumbOffset) {
    }

    @Override
    public void setThumb(Drawable thumb) {
        thumbDrawable = thumb;
    }

    @Override
    public void setProgressDrawable(Drawable d) {
        if (d instanceof LayerDrawable) {
            LayerDrawable ld = (LayerDrawable) d;
            Drawable drawable = ld.findDrawableByLayerId(android.R.id.background);
            if (drawable != null) {
                bacProgressDrawable = drawable;
            }
            //必须设置clipDrawable  否则渐变不生效  如果不设置 这里会发生有转型导致的崩溃
            ClipDrawable clipDrawableProgress = (ClipDrawable) ld.findDrawableByLayerId(android.R.id.progress);
            if (clipDrawableProgress != null) {
                progressDrawable = clipDrawableProgress;
                //不设置 不显示
                clipDrawableProgress.setLevel(10000);
            }

        } else {
            bacProgressDrawable = d;
            progressDrawable = d;
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        return true; //禁用触摸事件
    }

    /**
     * 覆盖掉原生的监听事件,用原生的监听实现自己的逻辑
     */
    private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }


    public void calculateProgress(int thumbScrollX) {
        //计算进度
        try {
            currentProgress = (thumbScrollX * maxProgress) / (width - tDWidth);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lastProgress != currentProgress) {
            getProgress();
            Log.e("currentProgress", "currentProgress:" + currentProgress);
        }
        lastProgress = currentProgress;
    }

}
