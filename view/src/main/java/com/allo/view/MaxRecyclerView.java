package com.allo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @ProjectName: Contacts
 * @Package: com.allo.view
 * @ClassName: MaxRecyclerView
 * @Description: 最大高度 RecyclerView
 * @Author: Roy
 * @CreateDate: 2021/4/28 上午10:28
 * @Version: 1.3
 */
public class MaxRecyclerView extends RecyclerView {

    private int mMaxHeight;

    public MaxRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public MaxRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaxRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MaxRecyclerView);
        mMaxHeight = a.getLayoutDimension(R.styleable.MaxRecyclerView_maxHeight, mMaxHeight);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);

        int height = getMeasuredHeight();
        if (mMaxHeight > 0 && height > mMaxHeight) {
            setMeasuredDimension(widthSpec, mMaxHeight);
        }
    }


}
