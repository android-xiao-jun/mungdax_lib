package com.zhihu.matisse.listener;

import androidx.fragment.app.FragmentActivity;

import kotlin.jvm.functions.Function1;

/**
 * desc:
 * verson:
 * create by zhijun on 2024/11/26 14:52
 * update by zhijun on 2024/11/26 14:52
 */
public interface OnRequestPermissionListener {
    //请求相机权限
    void requestPermissionCamera(FragmentActivity activity,Function1<Boolean, Boolean> function1);
    //请求相机
    void jumpCustomCamera(Function1<Boolean, Boolean> function1);
}
