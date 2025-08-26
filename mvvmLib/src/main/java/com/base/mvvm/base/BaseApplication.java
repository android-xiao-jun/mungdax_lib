package com.base.mvvm.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.allo.utils.LanguageUtils;
import com.base.mvvm.BuildConfig;
import com.base.mvvm.utils.KLog;
import com.base.mvvm.utils.Utils;

import java.lang.reflect.Method;

import io.reactivex.plugins.RxJavaPlugins;

public class BaseApplication extends Application {
    private static Application sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        setApplication(this);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LanguageUtils.changeApplicationContext(this);
    }

    /**
     * 当主工程没有继承BaseApplication时，可以使用setApplication方法初始化BaseApplication
     *
     * @param application
     */
    public static synchronized void setApplication(@NonNull Application application) {
        sInstance = application;
        //初始化工具类
        Utils.init(application);

        if (!RxJavaPlugins.isLockdown()){
            RxJavaPlugins.setErrorHandler(throwable -> {
                throwable.printStackTrace();
                KLog.e(throwable);
            });
        }
        if (!io.reactivex.rxjava3.plugins.RxJavaPlugins.isLockdown()){
            io.reactivex.rxjava3.plugins.RxJavaPlugins.setErrorHandler(throwable -> {
                throwable.printStackTrace();
                KLog.e(throwable);
            });
        }
        Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                e.printStackTrace();
                KLog.e(e);
                if (defaultUncaughtExceptionHandler != null) {
                    defaultUncaughtExceptionHandler.uncaughtException(t, e);
                }
            }
        });

        //注册监听每个activity的生命周期,便于堆栈式管理
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
//                AppManager.getAppManager().addActivity(activity);
                AppManager.getAppManager().setRunning(true);
                KLog.i(activity.getClass().getSimpleName());
                if (!com.allo.utils.ActivityExtension.OPEN_RECORD){
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
                }
                if (!AppManager.getAppManager().empty() && (activity.getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0 && AppManager.getAppManager().contains(activity)) {
                    return;//如果是从后台回到前台，这里不添加
                }
                if (!(activity instanceof FragmentActivity)){
                    //不是这个FragmentActivity app 有问题
                    return;
                }
                AppManager.getAppManager().addActivity(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                dispatchEvent(Lifecycle.Event.ON_START);
                AppManager.getAppManager().setRunning(true);
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                dispatchEvent(Lifecycle.Event.ON_RESUME);
                AppManager.getAppManager().setRunning(true);
                KLog.i(activity.getClass().getSimpleName());
                if (AppManager.getAppManager().empty()) {
                    AppManager.getAppManager().addActivity(activity);
                }
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                dispatchEvent(Lifecycle.Event.ON_PAUSE);
                if (activity.isFinishing() || activity.isDestroyed()) {
                    onActivityDestroyed(activity);
                }
                AppManager.getAppManager().setRunning(!(activity == AppManager.getAppManager().currentActivity()));
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                dispatchEvent(Lifecycle.Event.ON_STOP);
                //AppManager.getAppManager().setRunning(!(activity == AppManager.getAppManager().currentActivity()));
            }

            @Override
            public void onActivitySaveInstanceState(
                    @NonNull Activity activity,
                    @NonNull Bundle outState
            ) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                //AppManager.getAppManager().setRunning(!(activity == AppManager.getAppManager().currentActivity()));
                AppManager.getAppManager().removeActivity(activity);
                KLog.i(activity.getClass().getSimpleName());
            }
        });
    }

    /**
     * 发送 Lifecycle event
     */
    private static void dispatchEvent(Lifecycle.Event event) {
        String methodName = null;
        switch (event) {
            case ON_START:
                methodName = "activityStarted";
                break;
            case ON_RESUME:
                methodName = "activityResumed";
                break;
            case ON_PAUSE:
                methodName = "activityPaused";
                break;
            case ON_STOP:
                methodName = "activityStopped";
                break;
        }

        if (null == methodName)
            return;

        try {
            Method method = ProcessLifecycleOwner.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(ProcessLifecycleOwner.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得当前app运行的Application
     */
    public static Application getInstance() {
        if (sInstance == null) {
            throw new NullPointerException("please inherit BaseApplication or call setApplication.");
        }
        return sInstance;
    }
}
