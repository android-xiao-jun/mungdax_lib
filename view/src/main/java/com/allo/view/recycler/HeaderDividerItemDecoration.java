package com.allo.view.recycler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.allo.utils.Utils;
import com.allo.view.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

public class HeaderDividerItemDecoration extends RecyclerView.ItemDecoration {

    private final int leftMargin;
    private final int rightMargin;
    private final int height;
    private final int color;
    private final Paint mDividerPaint;

    public boolean showHeader  = true;

    private HeaderDividerItemDecoration(Builder builder) {
        leftMargin = builder.marginLeft;
        rightMargin = builder.marginRight;
        height = builder.height;
        color = builder.color == -1 ? ContextCompat.getColor(Utils.getApp(), R.color.color_decoration) : builder.color;
        mDividerPaint = new Paint();
        mDividerPaint.setColor(color);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return;
        }

        if (((LinearLayoutManager) layoutManager).getOrientation() == VERTICAL) {
//            drawVertical(c, parent);
        } else {
//            drawHorizontal(c, parent);
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return;
        }
        if (((LinearLayoutManager) layoutManager).getOrientation() == VERTICAL) {
            drawVertical(c, parent);
        } else {
//            drawHorizontal(c, parent);
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
//        canvas.save();
        int left, right;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (showHeader && i==0){
                continue;
            }
            final View child = parent.getChildAt(i);
            left = getNotZero(leftMargin, child.getPaddingLeft());
            right = child.getRight() - getNotZero(rightMargin, child.getPaddingRight());
            canvas.drawRect(left, child.getBottom(), right, child.getBottom() + height, mDividerPaint);
        }
//        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) return;
        if (((LinearLayoutManager) layoutManager).getOrientation() == VERTICAL) {
            outRect.set(0, 0, 0, height);
        } else {
            outRect.set(0, 0, height, 0);
        }
    }

    public static class Builder {
        private int marginLeft = 0;
        private int marginRight = 0;
        private int height = 1;
        private int color = -1;

        public Builder() {
        }

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setMarginLeft(int marginLeft) {
            this.marginLeft = marginLeft;
            return this;
        }

        public Builder setMarginRight(int marginRight) {
            this.marginRight = marginRight;
            return this;
        }

        public HeaderDividerItemDecoration build() {
            return new HeaderDividerItemDecoration(this);
        }
    }

    private int getNotZero(int first, int second) {
        return first == 0 ? second : first;
    }
}
