package com.allo.view.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.allo.utils.SizeUtils;
import com.allo.view.BuildConfig;
import com.allo.view.R;

public abstract class BaseBottomSheetDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //去掉 dialog 的标题，需要在 setContentView() 之前
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
        // 只能在 onCreateView 中设置 window , onCreate 无效
        final Window window = getDialog().getWindow();
        if (window != null) {
            // 不设置的话 window attrs 会无效
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            final WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            //设置 dialog 在底部
            lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            //设置 dialog 动画
            lp.windowAnimations = R.style.ActionSheetDialogAnimation;

            //去掉 dialog 默认的 padding
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setAttributes(lp);
            if (!com.allo.utils.ActivityExtension.OPEN_RECORD){
                window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE);
            }
        }
        final View rootView = bindContentView(inflater, container);
        rootView.setMinimumWidth(SizeUtils.Companion.getScreenWidth());
        onCreateView(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            final Window w = getDialog().getWindow();
            if (w != null) {
                // 解决无法铺满屏幕问题
                w.setLayout(SizeUtils.Companion.getScreenWidth(), w.getAttributes().height);
            }
        }
    }


    @SuppressWarnings("unchecked")
    protected <T> T findView(View parent, @IdRes int id) {
        return (T) parent.findViewById(id);
    }

    /**
     * @return dialog layout res id
     */
    protected abstract int getContentViewResId();

    protected View bindContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(getContentViewResId(), container, false);
    }

    /**
     * 该方法在 onCreateView 时调用
     * 一般不再重写 onCreateView(LayoutInflater inflater...);
     */
    protected abstract void onCreateView(View rootView);
}
