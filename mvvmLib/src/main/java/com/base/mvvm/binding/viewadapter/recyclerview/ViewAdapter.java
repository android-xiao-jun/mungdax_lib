package com.base.mvvm.binding.viewadapter.recyclerview;

import android.annotation.SuppressLint;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.base.mvvm.binding.command.BindingCommand;

public class ViewAdapter {

    @BindingAdapter("lineManager")
    public static void setLineManager(RecyclerView recyclerView, LineManagers.LineManagerFactory lineManagerFactory) {
        recyclerView.addItemDecoration(lineManagerFactory.create(recyclerView));
    }


    @BindingAdapter(value = {"onScrollChangeCommand", "onScrollStateChangedCommand"}, requireAll = false)
    public static void onScrollChangeCommand(final RecyclerView recyclerView,
                                             final BindingCommand<ScrollDataWrapper> onScrollChangeCommand,
                                             final BindingCommand<Integer> onScrollStateChangedCommand) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int state;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (onScrollChangeCommand != null) {
                    onScrollChangeCommand.execute(new ScrollDataWrapper(dx, dy, state));
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                state = newState;
                if (onScrollStateChangedCommand != null) {
                    onScrollStateChangedCommand.execute(newState);
                }
            }
        });

    }

    @SuppressWarnings("unchecked")
    @BindingAdapter({"onLoadMoreCommand"})
    public static void onLoadMoreCommand(final RecyclerView recyclerView, final BindingCommand<Integer> onLoadMoreCommand) {
        RecyclerView.OnScrollListener listener = new OnScrollListener(onLoadMoreCommand);
        recyclerView.addOnScrollListener(listener);

    }

    @BindingAdapter("itemAnimator")
    public static void setItemAnimator(RecyclerView recyclerView, RecyclerView.ItemAnimator animator) {
        recyclerView.setItemAnimator(animator);
    }

    public static class OnScrollListener extends RecyclerView.OnScrollListener {

//        private PublishSubject<Integer> methodInvoke = PublishSubject.create();

        private final BindingCommand<Integer> onLoadMoreCommand;
        private int lastInt = -1;
        private long lastRefresh = 0L;

        @SuppressLint("CheckResult")
        public OnScrollListener(final BindingCommand<Integer> onLoadMoreCommand) {
            this.onLoadMoreCommand = onLoadMoreCommand;

            // 1秒内只响应一次 如果上一次loadMore和下一次loadMore间隔小于1秒就会无响应
            /*methodInvoke.throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            Log.i("yforyoung", "accept: " + integer);
                            onLoadMoreCommand.execute(integer);
                        }
                    });*/
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int pastVisiblesItems = findFirstVisibleItem(layoutManager);
            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                if (onLoadMoreCommand != null) {
//                    methodInvoke.onNext(recyclerView.getAdapter().getItemCount());
                    int count = recyclerView.getAdapter().getItemCount();
                    if (lastInt != count || System.currentTimeMillis() - lastRefresh > 2000L) {
                        lastInt = count;
                        lastRefresh = System.currentTimeMillis();
                        onLoadMoreCommand.execute(count);
                    }
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }


    }

    public static int findFirstVisibleItem(RecyclerView.LayoutManager manager){
        if (manager instanceof LinearLayoutManager){
            return ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
        }else if (manager instanceof StaggeredGridLayoutManager){
            return findFirstPos(((StaggeredGridLayoutManager) manager).findFirstVisibleItemPositions(null));
        }else {
            return 0;
        }
    }

    private static int findFirstPos(int[] pos) {
        int res = 0;
        if (pos == null || pos.length == 0) {
            return res;
        }

        res = pos[0];
        for (int i = 1, size = pos.length; i < size; i++) {
            if (pos[i] < res) {
                res = pos[i];
            }
        }
        return res >= 0 ? res : 0;
    }

    private static int findLastPos(int[] pos) {
        int res = 0;
        if (pos == null || pos.length == 0) {
            return res;
        }

        res = pos[0];
        for (int i = 1, size = pos.length; i < size; i++) {
            if (pos[i] > res) {
                res = pos[i];
            }
        }
        return res >= 0 ? res : 0;
    }


    public static class ScrollDataWrapper {
        public float scrollX;
        public float scrollY;
        public int state;

        public ScrollDataWrapper(float scrollX, float scrollY, int state) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
            this.state = state;
        }
    }
}
