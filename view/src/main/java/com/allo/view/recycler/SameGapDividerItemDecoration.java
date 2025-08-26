package com.allo.view.recycler;

import android.graphics.Rect;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import kotlin.Deprecated;

public class SameGapDividerItemDecoration extends RecyclerView.ItemDecoration {

    private int horizontalSpace;
    private int verticalSpace;
    private int mEdgeSpace = 0;
    private boolean handleFullSpan = false;

    protected void setHorizontalSpace(int horizontalSpace) {
        this.horizontalSpace = horizontalSpace;
    }

    protected void setVerticalSpace(int verticalSpace) {
        this.verticalSpace = verticalSpace;
    }

    public SameGapDividerItemDecoration() {
    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int position = parent.getChildAdapterPosition(view);
        int size = parent.getAdapter().getItemCount();
        if (layoutManager instanceof GridLayoutManager) {
            int spanIndex = 0;
            GridLayoutManager gridManager = (GridLayoutManager) layoutManager;
            if (view.getLayoutParams() instanceof GridLayoutManager.LayoutParams) {
                spanIndex = ((GridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
            }
            if (gridManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                // 横滑
                setGridOffset(GridLayoutManager.HORIZONTAL, gridManager.getSpanCount(), outRect, position, size,spanIndex);
            } else if (gridManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                // 竖滑
                setGridOffset(GridLayoutManager.VERTICAL, gridManager.getSpanCount(), outRect, position, size,spanIndex);
            }

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            // 全铺满的不针对宽度做处理
            boolean fullSpan = false;
            int spanIndex = 0;
            StaggeredGridLayoutManager staggeredGridManager = (StaggeredGridLayoutManager) layoutManager;
            if (view.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                if (!handleFullSpan && ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).isFullSpan()) {
                    fullSpan = true;
                }
                spanIndex = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
            }
            if (staggeredGridManager.getOrientation() == StaggeredGridLayoutManager.HORIZONTAL) {
                // 横滑
                setStaggeredGridOffset(GridLayoutManager.HORIZONTAL, staggeredGridManager.getSpanCount(), outRect, position, size, fullSpan, spanIndex);
            } else {
                // 竖滑
                setStaggeredGridOffset(GridLayoutManager.VERTICAL, staggeredGridManager.getSpanCount(), outRect, position, size, fullSpan, spanIndex);
            }

        } else if (layoutManager instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL) {
                setLinearOffset(LinearLayout.HORIZONTAL, outRect, position, size);
            } else {
                setLinearOffset(LinearLayout.VERTICAL, outRect, position, size);
            }
        }
    }

    private void setLinearOffset(int orientation, Rect outRect, int childPosition, int itemCount) {

        if (orientation == LinearLayoutManager.HORIZONTAL) {
            if (childPosition == 0) {
                // 第一个要设置PaddingLeft
                outRect.set(mEdgeSpace, 0, horizontalSpace, 0);
            } else if (childPosition == itemCount - 1) {
                // 最后一个设置PaddingRight
                outRect.set(0, 0, mEdgeSpace, 0);
            } else {
                outRect.set(0, 0, horizontalSpace, 0);
            }
        } else {
            if (childPosition == 0) {
                // 第一个要设置PaddingTop
                outRect.set(0, mEdgeSpace, 0, verticalSpace);
            } else if (childPosition == itemCount - 1) {
                // 最后一个要设置PaddingBottom
                outRect.set(0, 0, 0, mEdgeSpace);
            } else {
                outRect.set(0, 0, 0, verticalSpace);
            }
        }
    }

    private void setGridOffset(int orientation, int spanCount, Rect outRect, int childPosition, int itemCount,int spanIndex) {
        int column = spanIndex % spanCount; // 列数
        int row = childPosition / spanCount;// 行数
        float left;
        float right;
        float top;
        float bottom;
        if (orientation == GridLayoutManager.VERTICAL) {
            float totalSpace = horizontalSpace * (spanCount - 1) + mEdgeSpace * 2; // 总共的padding值
            float eachSpace = totalSpace / spanCount; // 分配给每个item的padding值
            top = 0; // 默认 top为0
            bottom = verticalSpace; // 默认bottom为间距值
            if (mEdgeSpace == 0) {
                left = column * eachSpace / (spanCount - 1);
                right = eachSpace - left;
                // 无边距的话  只有最后一行bottom为0
                if (childPosition == -1 || (itemCount - 1) / spanCount == row) {
                    bottom = 0;
                }
            } else {
                // 注释掉： 不显示上下边距
                /*if (childPosition < spanCount) {
                    // 有边距的话 第一行top为边距值
                    top = mEdgeSpace;
                } else if (itemCount / spanCount == row) {
                    // 有边距的话 最后一行bottom为边距值
                    bottom = mEdgeSpace;
                }*/
                left = column * (eachSpace - mEdgeSpace - mEdgeSpace) / (spanCount - 1) + mEdgeSpace;
                right = eachSpace - left;
            }
        } else {
            float totalSpace = verticalSpace * (spanCount - 1) + mEdgeSpace * 2; // 总共的padding值
            float eachSpace = totalSpace / spanCount; // 分配给每个item的padding值
            // orientation == GridLayoutManager.HORIZONTAL 跟上面的大同小异, 将top,bottom替换为left,right即可
            left = 0;
            right = horizontalSpace;
            if (mEdgeSpace == 0) {
                top = column * eachSpace / (spanCount - 1);
                bottom = eachSpace - top;
                if ((itemCount - 1) / spanCount == row) {
                    right = 0;
                }
            } else {
                if (childPosition < spanCount) {
                    left = mEdgeSpace;
                } else if ((itemCount - 1) / spanCount == row) {
                    right = mEdgeSpace;
                }
                top = column * (eachSpace - mEdgeSpace - mEdgeSpace) / (spanCount - 1) + mEdgeSpace;
                bottom = eachSpace - top;
            }
        }
        outRect.set((int) left, (int) top, (int) right, (int) bottom);
    }

    protected void setStaggeredGridOffset(int orientation, int spanCount, Rect outRect, int childPosition, int itemCount, boolean fullSpan, int spanIndex) {
        int column = spanIndex % spanCount; // 列数
        int row = childPosition / spanCount;// 行数
        float left;
        float right;
        float top;
        float bottom;
        if (orientation == GridLayoutManager.VERTICAL) {
            float totalSpace = horizontalSpace * (spanCount - 1) + mEdgeSpace * 2; // 总共的padding值
            float eachSpace = totalSpace / spanCount; // 分配给每个item的padding值
            top = 0; // 默认 top为0
            bottom = verticalSpace; // 默认bottom为间距值
            if (mEdgeSpace == 0) {
                left = column * eachSpace / (spanCount - 1);
                right = eachSpace - left;
                // 无边距的话  只有最后一行bottom为0
                if (itemCount / spanCount == row) {
                    bottom = 0;
                }
            } else {
                if (childPosition < spanCount) {
                    // 有边距的话 第一行top为边距值
                    top = mEdgeSpace;
                } else if (itemCount / spanCount == row) {
                    // 有边距的话 最后一行bottom为边距值
                    bottom = mEdgeSpace;
                }
                left = column * (eachSpace - mEdgeSpace - mEdgeSpace) / (spanCount - 1) + mEdgeSpace;
                right = eachSpace - left;
            }
            // 不处理fullSpan的数据宽度
            if (fullSpan && !handleFullSpan) {
                left = 0;
                right = 0;
            }
        } else {
            float totalSpace = verticalSpace * (spanCount - 1) + mEdgeSpace * 2; // 总共的padding值
            float eachSpace = totalSpace / spanCount; // 分配给每个item的padding值
            // orientation == GridLayoutManager.HORIZONTAL 跟上面的大同小异, 将top,bottom替换为left,right即可
            left = 0;
            right = horizontalSpace;
            if (mEdgeSpace == 0) {
                top = column * eachSpace / (spanCount - 1);
                bottom = eachSpace - top;
                if (itemCount / spanCount == row) {
                    right = 0;
                }
            } else {
                if (childPosition < spanCount) {
                    left = mEdgeSpace;
                } else if (itemCount / spanCount == row) {
                    right = mEdgeSpace;
                }
                top = column * (eachSpace - mEdgeSpace - mEdgeSpace) / (spanCount - 1) + mEdgeSpace;
                bottom = eachSpace - top;
            }
            // 不处理fullSpan的数据高度
            if (fullSpan && !handleFullSpan) {
                top = 0;
                bottom = 0;
            }
        }
        outRect.set((int) left, (int) top, (int) right, (int) bottom);
    }

    @Deprecated(message = "useless method")
    public void setHorizontalSpanCount(int horizontalSpanCount) {
    }

    @Deprecated(message = "useless method")
    public void setVerticalSpanCount(int verticalSpanCount) {
    }

    public void setEdgeSpace(int mEdgeSpace) {
        this.mEdgeSpace = mEdgeSpace;
    }

    public void setHandleFullSpan(boolean handleFullSpan) {
        this.handleFullSpan = handleFullSpan;
    }

    public static class Builder {
        private int horizontalSpace;
        private int verticalSpace;
        private int horizontalSpanCount = 1;
        private int verticalSpanCount = 1;
        private int edgeSpace;
        private boolean handleFullSpan;

        public Builder setHandleFullSpan(boolean handle) {
            handleFullSpan = handle;
            return this;
        }

        public Builder setEdgeSpace(int edgeSpace) {
            this.edgeSpace = edgeSpace;
            return this;
        }

        public Builder setHorizontalSpace(int horizontalSpace) {
            this.horizontalSpace = horizontalSpace;
            return this;
        }

        public Builder setVerticalSpace(int verticalSpace) {
            this.verticalSpace = verticalSpace;
            return this;
        }

        @Deprecated(message = "useless method")
        public Builder setHorizontalSpanCount(int horizontalSpanCount) {
            return this;
        }

        @Deprecated(message = "useless method")
        public Builder setVerticalSpanCount(int verticalSpanCount) {
            return this;
        }


        public SameGapDividerItemDecoration build() {
            SameGapDividerItemDecoration decoration = new SameGapDividerItemDecoration();
            decoration.setHorizontalSpace(horizontalSpace);
            decoration.setVerticalSpace(verticalSpace);
            decoration.setHorizontalSpanCount(horizontalSpanCount);
            decoration.setVerticalSpanCount(verticalSpanCount);
            decoration.setEdgeSpace(edgeSpace);
            decoration.setHandleFullSpan(handleFullSpan);
            return decoration;
        }

    }
}
