package com.allo.view.recycler;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseHeaderSectionItemDecoration extends RecyclerView.ItemDecoration {
    public static final int DEFAULT_SECTION_START_POSITION = 0;

    protected Paint mPaint;
    protected int mStartPostion;
    protected int mHeadHeight;
    protected int mBackgroundColor;
    protected int mTextColor;
    protected int mTextSize;
    protected int startMargin;
    protected boolean mIsAllowStickyHeader;
    protected Rect mTextBound;
    protected GroupChangeListener changeListener;

    private int currentGroupIndex;

    public BaseHeaderSectionItemDecoration(Builder builder) {
        mHeadHeight = builder.headerHeight;
        mStartPostion = builder.startPosition != -1 ?
                builder.startPosition : DEFAULT_SECTION_START_POSITION;

        mTextSize = builder.textSize;
        mTextColor = builder.textColor;
        mBackgroundColor = builder.backgroundColor;
        mIsAllowStickyHeader = builder.isAllowStickyHeader;
        startMargin = builder.startMargin;
        changeListener = builder.changeListener;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mTextBound = new Rect();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        final int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        if (hasHeader(position)) {
            outRect.set(0, mHeadHeight, 0, 0);
        } else {
            // 一般是不留空位
            outRect.set(0, 0, 0, 0);
        }
    }

    /**
     * 分类的 header
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int size = parent.getChildCount();

        for (int i = 0; i < size; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int position = lp.getViewLayoutPosition();
            if (position > RecyclerView.NO_POSITION && hasHeader(position)) {
                drawHeader(c, left, child.getTop() - lp.topMargin - mHeadHeight,
                        right, child.getTop() - lp.topMargin, child, lp, position);
            }
        }
    }

    /**
     * 悬浮 Sticky Header
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (!mIsAllowStickyHeader && parent.getChildCount() < 2) {
            return;
        }
        // top two visible child view
        final View currentChild = parent.getChildAt(0);
        final View nextChild = parent.getChildAt(1);

        if (currentChild != null && nextChild != null) {
            int current = parent.getChildAdapterPosition(currentChild);
            int next = parent.getChildAdapterPosition(nextChild);

            if (current < mStartPostion) {
                // no need for sticky header
                return;
            }
            changeListener.changePosition(current);

            final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) currentChild.getLayoutParams();
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();
            int bottom = mHeadHeight;
            /**
             * 判断交替, 移出屏幕外的 child 满足条件:
             * 1. 是其所属分组的最后一个 view
             * 2. child view's bottom < sticky header height
             * 符合则 sticky header 跟随滑动 (包括 向上划出 或者 向下划入 两种滑动状态) ,
             * 否则保持固定
             */
            if (isEndOfGroup(current) && hasHeader(next) && currentChild.getBottom() + lp.bottomMargin <= mHeadHeight) {
                bottom = currentChild.getBottom() + lp.bottomMargin;
                currentGroupIndex = current;
//                if (changeListener!=null){
//                    if (bottom < 5){
//                        changeListener.changePosition(currentGroupIndex);
//                    }else if (bottom > mHeadHeight-5){
//                        changeListener.changePosition(next-mStartPostion);
//                    }
//                }
//                Log.i("yangchong", "bottom: "+bottom +"    current:"+current + " next:"+next);
            }

            drawHeader(c, left, bottom - mHeadHeight, right, bottom, currentChild, lp, current);
        }
    }

    /**
     * 当前位置是否属于新的分类, 添加 header
     */
    protected abstract boolean hasHeader(int position);

    /**
     * 当前是否是该分类下的最后一个, 判断下一个分类的 header 联动替换
     */
    protected abstract boolean isEndOfGroup(int position);

    /**
     * 绘制 header 的具体方法
     */
    protected abstract void drawHeader(Canvas c, int left, int top, int right, int bottom,
                                       View child, RecyclerView.LayoutParams lp, int position);

    public abstract static class Builder<T extends RecyclerView.ItemDecoration, B extends Builder<T, B>> {
        private int headerHeight = 0;
        private int startPosition = -1;
        private int backgroundColor = Color.WHITE;
        private int textColor = Color.BLACK;
        private int textSize = 13;
        private int startMargin = -1;
        private boolean isAllowStickyHeader = true;
        private GroupChangeListener changeListener = null;

        @NonNull
        public B setHeaderHeight(int headerHeight) {
            this.headerHeight = headerHeight;
            return getThis();
        }

        @NonNull
        public B setStartPosition(int startPosition) {
            this.startPosition = startPosition;
            return getThis();
        }

        @NonNull
        public B setBackgroundColor(@ColorInt int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return getThis();
        }

        @NonNull
        public B setTextColor(@ColorInt int textColor) {
            this.textColor = textColor;
            return getThis();
        }

        @NonNull
        public B setTextSize(int textSize) {
            this.textSize = textSize;
            return getThis();
        }

        @NonNull
        public B setStartMargin(int startMargin) {
            this.startMargin = startMargin;
            return getThis();
        }

        @NonNull
        public B setAllowStickyHeader(boolean allowStickyHeader) {
            isAllowStickyHeader = allowStickyHeader;
            return getThis();
        }

        @NonNull
        public B setGroupChangeListener(GroupChangeListener listener) {
            this.changeListener = listener;
            return getThis();
        }

        public abstract B getThis();

        public abstract T build();
    }

    public interface GroupChangeListener {
        void changePosition(int position);
    }

}
