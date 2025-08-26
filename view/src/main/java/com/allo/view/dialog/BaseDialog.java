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

import com.allo.view.BuildConfig;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by Lynn on 2017/7/31.
 */

public abstract class BaseDialog extends DialogFragment {

    public float widthPercent = 0.752f;


    public void setWidthPercent(float widthPercent) {
        this.widthPercent = widthPercent;
    }


    public void setFullScreen(){

    }

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
            window.setSoftInputMode(WindowManager
                    .LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            final WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.gravity = gravity();
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setAttributes(lp);
            if (!com.allo.utils.ActivityExtension.OPEN_RECORD){
                window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE);
            }
        }
        final View rootView = bindContentView(inflater, container);
        onCreateView(rootView);
        return rootView;
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    public int gravity(){
        return Gravity.CENTER;
    }

    /**
     * @return dialog layout res id
     */
    protected int getContentViewResId() {
        return -1;
    }

    protected View bindContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {

        return inflater.inflate(getContentViewResId(), container, false);
    }

    /**
     * 该方法在 onCreateView 时调用
     * 一般不再重写 onCreateView(LayoutInflater inflater...);
     */
    protected abstract void onCreateView(View rootView);

    public void show(FragmentActivity context, String tag) {
        if (isAdded() || context.isFinishing() || context.isDestroyed()) return;
        try {
            show(context.getSupportFragmentManager(), tag);
        } catch (Throwable e) {
            FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        }
    }

    public void show(Fragment fragment,String tag){
        if (fragment.getActivity()==null) return;
        if (isAdded() || fragment.getActivity().isFinishing() || fragment.getActivity().isDestroyed()) return;
        try {
            show(fragment.getChildFragmentManager(), tag);
        } catch (Throwable e) {
            FragmentTransaction ft = fragment.getChildFragmentManager().beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        }
    }
}
