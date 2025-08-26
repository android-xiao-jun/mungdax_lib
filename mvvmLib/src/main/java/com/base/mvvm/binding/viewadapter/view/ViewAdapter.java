package com.base.mvvm.binding.viewadapter.view;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.mvvm.base.BaseApplication;
import com.base.mvvm.binding.command.ResponseCommand;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.base.mvvm.binding.command.BindingCommand;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

public class ViewAdapter {
    //防重复点击间隔(秒)
    public static final int CLICK_INTERVAL = 1;

    @BindingAdapter(value = {"layoutWidth", "layoutHeight"}, requireAll = false)
    public static void layoutSize(View view, final int width, final int height) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (width != 0)
            lp.width = width;
        if (height != 0)
            lp.height = height;
        view.setLayoutParams(lp);
    }

    @BindingAdapter(value = {"layoutMaxWidth"}, requireAll = false)
    public static void layoutMaxWidth(View view, final int width) {
        if (view instanceof TextView) {
            if (width == -1) {
                ((TextView) view).setMaxWidth(Integer.MAX_VALUE);
            } else {
                ((TextView) view).setMaxWidth(width);
            }
        }
        /// TODO...
    }



    /**
     * requireAll 是意思是是否需要绑定全部参数, false为否
     * View的onClick事件绑定
     * onClickCommand 绑定的命令,
     * isThrottleFirst 是否开启防止过快点击
     */
    @BindingAdapter(value = {"onClickCommand", "isThrottleFirst"}, requireAll = false)
    public static void onClickCommand(View view, final BindingCommand clickCommand, final boolean isThrottleFirst) {
        if (isThrottleFirst) {
            RxView.clicks(view)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object object) throws Exception {
                            if (clickCommand != null) {
                                clickCommand.execute(view);
                            }
                        }
                    });
        } else {
            RxView.clicks(view)
                    .throttleFirst(CLICK_INTERVAL, TimeUnit.SECONDS)//1秒钟内只允许点击1次
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object object) throws Exception {
                            if (clickCommand != null) {
                                clickCommand.execute(view);
                            }
                        }
                    });
        }
    }

    /**
     * view的onLongClick事件绑定
     */
    @BindingAdapter(value = {"onLongClickCommand"}, requireAll = false)
    public static void onLongClickCommand(View view, final BindingCommand clickCommand) {
        RxView.longClicks(view)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {
                        if (clickCommand != null) {
                            //clickCommand.execute();
                            clickCommand.execute(view);
                        }
                    }
                });
    }

    /**
     * 回调控件本身
     *
     * @param currentView
     * @param bindingCommand
     */
    @BindingAdapter(value = {"currentView"}, requireAll = false)
    public static void replyCurrentView(View currentView, BindingCommand bindingCommand) {
        if (bindingCommand != null) {
            bindingCommand.execute(currentView);
        }
    }

    /**
     * view是否需要获取焦点
     */
    @BindingAdapter({"requestFocus"})
    public static void requestFocusCommand(View view, final Boolean needRequestFocus) {
        if (needRequestFocus) {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        } else {
            view.clearFocus();
        }
    }

    /**
     * view的焦点发生变化的事件绑定
     */
    @BindingAdapter({"onFocusChangeCommand"})
    public static void onFocusChangeCommand(View view, final BindingCommand<Boolean> onFocusChangeCommand) {
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (onFocusChangeCommand != null) {
                    onFocusChangeCommand.execute(hasFocus);
                }
            }
        });
    }

    /**
     * view的显示隐藏
     */
    @BindingAdapter(value = {"isVisible"}, requireAll = false)
    public static void isVisible(View view, @Nullable final Boolean visibility) {
        if (visibility != null && visibility) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter(value = {"loadGif"}, requireAll = false)
    public static void loadGif(View view, @Nullable Drawable gif){
        if (view != null) {
            Glide.with(view.getContext()).load(gif).into((ImageView) view);
        }
    }


    /**
     * view的显示隐藏
     */
    @BindingAdapter(value = {"isVisible2"}, requireAll = false)
    public static void isVisible2(View view, @Nullable final Boolean visibility) {
        if (visibility != null && visibility) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }
    @BindingAdapter({"onTouchCommand"})
    public static void onTouchCommand(View view, final ResponseCommand<MotionEvent, Boolean> onTouchCommand) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (onTouchCommand != null) {
                    try {
                        return onTouchCommand.execute(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
    }

    @BindingAdapter({"onSelected"})
    public static void onSelected(View view, final boolean isSelected) {
        view.setSelected(isSelected);
    }

    @BindingAdapter({"onTag"})
    public static void onTag(View view, final String tag) {
        view.setTag(tag);
    }
}
