package com.base.mvvm.base;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

public abstract class BottomSheetDialog<V extends ViewDataBinding, VM extends BaseViewModel> extends BaseDialogFragment<V, VM> {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //去掉 dialog 的标题，需要在 setContentView() 之前
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
        // 只能在 onCreateView 中设置 window , onCreate 无效
        final Window window = getDialog().getWindow();
        if (window != null) {
            // 不设置的话 window attrs 会无效
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            final WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = getWindowWidth();
            lp.height = getWindowHeight();
            //设置 dialog 在底部
            lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            //设置 dialog 动画
            if (windowAnimation() != 0) {
                lp.windowAnimations = windowAnimation();
            }
            //lp.windowAnimations = R.style.ActionSheetDialogAnimation;

            //去掉 dialog 默认的 padding
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setAttributes(lp);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            final Window w = getDialog().getWindow();
            if (w != null) {
                // 解决无法铺满屏幕问题
                w.setLayout(getScreenWidth(), getWindowHeight());
            }
        }
    }

    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public abstract int windowAnimation();

    public int getWindowWidth(){
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }
    public int getWindowHeight(){
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }
}
