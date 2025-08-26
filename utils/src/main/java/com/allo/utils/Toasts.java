package com.allo.utils;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.lang.ref.WeakReference;

/**
 * 吐司工具类
 */
public final class Toasts {

    private static final int DEFAULT_COLOR = 0x12000000;
    //private static Toast sToast;
    //private static int gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
    private static int gravity = Gravity.CENTER;
    private static int xOffset = 0;
    private static int yOffset = 0;//(int) (64 * Utils.getApp().getResources().getDisplayMetrics().density + 0.5);
    private static int backgroundColor = DEFAULT_COLOR;
    private static int bgResource = -1;
    private static int messageColor = DEFAULT_COLOR;
    //    private static View sViewWeakReference;
    private static int toastLayoutId = 0;
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private static WeakReference<Toast> sToastWeakReference;

    private Toasts() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 设置吐司位置
     *
     * @param gravity 位置
     * @param xOffset x偏移
     * @param yOffset y偏移
     */
    public static void setGravity(int gravity, int xOffset, int yOffset) {
        Toasts.gravity = gravity;
        Toasts.xOffset = xOffset;
        Toasts.yOffset = yOffset;
    }

    /**
     * 设置吐司view
     *
     * @param layoutId 视图
     */
    public static void setView(@LayoutRes int layoutId) {
//        LayoutInflater inflate = (LayoutInflater) Utils.getApp().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        //sViewWeakReference = new WeakReference<>(inflate.inflate(layoutId, null));
//        sViewWeakReference = inflate.inflate(layoutId, null);

        toastLayoutId = layoutId;
    }

    /**
     * 创建toastView
     *
     * @return view
     */
    @Nullable
    private static View createToastView() {
        if (toastLayoutId != 0) {
            return LayoutInflater.from(Utils.getApp()).inflate(toastLayoutId, null);
        } else {
            return null;
        }
    }

//
//    /**
//     * 设置吐司view
//     *
//     * @param view 视图
//     */
//    public static void setView(@Nullable View view) {
//        //sViewWeakReference = view == null ? null : new WeakReference<>(view);
//        sViewWeakReference = view;
//    }
//    /**
//     * 获取吐司view
//     *
//     * @return view
//     */
//    public static View getView() {
//        if (sViewWeakReference != null) {
//            /*final View view = sViewWeakReference.get();
//            if (view != null) {
//                return view;
//            }*/
//            return sViewWeakReference;
//        }
//
//        if (sToastWeakReference != null) {
//            Toast t = sToastWeakReference.get();
//            if (t != null) {
//                return t.getView();
//            }
//        }
//
//        //if (sToast != null) return sToast.getView();
//        return null;
//    }

    /**
     * 设置背景颜色
     *
     * @param backgroundColor 背景色
     */
    public static void setBackgroundColor(@ColorInt int backgroundColor) {
        Toasts.backgroundColor = backgroundColor;
    }

    /**
     * 设置背景资源
     *
     * @param bgResource 背景资源
     */
    public static void setBgResource(@DrawableRes int bgResource) {
        Toasts.bgResource = bgResource;
    }

    /**
     * 设置消息颜色
     *
     * @param messageColor 颜色
     */
    public static void setMessageColor(@ColorInt int messageColor) {
        Toasts.messageColor = messageColor;
    }

    /**
     * 安全地显示短时吐司
     *
     * @param text 文本
     */
    public static void showShortSafe(final CharSequence text) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                show(text, Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * 安全地显示短时吐司
     *
     * @param resId 资源Id
     */
    public static void showShortSafe(final @StringRes int resId) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                show(resId, Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * 安全地显示短时吐司
     *
     * @param resId 资源Id
     * @param args  参数
     */
    public static void showShortSafe(final @StringRes int resId, final Object... args) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                show(resId, Toast.LENGTH_SHORT, args);
            }
        });
    }

    /**
     * 安全地显示短时吐司
     *
     * @param format 格式
     * @param args   参数
     */
    public static void showShortSafe(final String format, final Object... args) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                show(format, Toast.LENGTH_SHORT, args);
            }
        });
    }

    /**
     * 安全地显示长时吐司
     *
     * @param text 文本
     */
    public static void showLongSafe(final CharSequence text) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                show(text, Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * 安全地显示长时吐司
     *
     * @param resId 资源Id
     */
    public static void showLongSafe(final @StringRes int resId) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                show(resId, Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * 安全地显示长时吐司
     *
     * @param resId 资源Id
     * @param args  参数
     */
    public static void showLongSafe(final @StringRes int resId, final Object... args) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                show(resId, Toast.LENGTH_LONG, args);
            }
        });
    }

    /**
     * 安全地显示长时吐司
     *
     * @param format 格式
     * @param args   参数
     */
    public static void showLongSafe(final String format, final Object... args) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                show(format, Toast.LENGTH_LONG, args);
            }
        });
    }

    /**
     * 显示短时吐司
     *
     * @param text 文本
     */
    public static void showShort(CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    /**
     * 显示短时吐司
     *
     * @param resId 资源Id
     */
    public static void showShort(@StringRes int resId) {
        show(resId, Toast.LENGTH_SHORT);
    }

    /**
     * 显示短时吐司
     *
     * @param resId 资源Id
     * @param args  参数
     */
    public static void showShort(@StringRes int resId, Object... args) {
        show(resId, Toast.LENGTH_SHORT, args);
    }

    /**
     * 显示短时吐司
     *
     * @param format 格式
     * @param args   参数
     */
    public static void showShort(String format, Object... args) {
        show(format, Toast.LENGTH_SHORT, args);
    }

    /**
     * 显示长时吐司
     *
     * @param text 文本
     */
    public static void showLong(CharSequence text) {
        show(text, Toast.LENGTH_LONG);
    }

    /**
     * 显示长时吐司
     *
     * @param resId 资源Id
     */
    public static void showLong(@StringRes int resId) {
        show(resId, Toast.LENGTH_LONG);
    }

    /**
     * 显示长时吐司
     *
     * @param resId 资源Id
     * @param args  参数
     */
    public static void showLong(@StringRes int resId, Object... args) {
        show(resId, Toast.LENGTH_LONG, args);
    }

    /**
     * 显示长时吐司
     *
     * @param format 格式
     * @param args   参数
     */
    public static void showLong(String format, Object... args) {
        show(format, Toast.LENGTH_LONG, args);
    }

    /**
     * 安全地显示短时自定义吐司
     */
    public static void showCustomShortSafe() {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                show("", Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * 安全地显示长时自定义吐司
     */
    public static void showCustomLongSafe() {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                show("", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * 显示短时自定义吐司
     */
    public static void showCustomShort() {
        show("", Toast.LENGTH_SHORT);
    }

    /**
     * 显示长时自定义吐司
     */
    public static void showCustomLong() {
        show("", Toast.LENGTH_LONG);
    }

    /**
     * 显示吐司
     *
     * @param resId    资源Id
     * @param duration 显示时长
     */
    private static void show(@StringRes int resId, int duration) {
        show(Utils.getApp().getResources().getText(resId).toString(), duration);
    }

    /**
     * 显示吐司
     *
     * @param resId    资源Id
     * @param duration 显示时长
     * @param args     参数
     */
    private static void show(@StringRes int resId, int duration, Object... args) {
        String str = "";
        try {
            str = String.format(Utils.getApp().getResources().getString(resId), args);
        } catch (Exception e) {
            return;
        }
        show(str, duration);
    }

    /**
     * 显示吐司
     *
     * @param format   格式
     * @param duration 显示时长
     * @param args     参数
     */
    private static void show(String format, int duration, Object... args) {
        String str = "";
        try {
            str = String.format(format, args);
        } catch (Exception e) {
            return;
        }
        show(str, duration);
    }

    public static void show(CharSequence text, int duration) {
        if (text == null || text.length() == 0){
            return;
        }
        if (Thread.currentThread() != Looper.getMainLooper().getThread()){
            sHandler.post(() -> show(text, duration));
            return;
        }
        cancel();
        boolean isCustom = false;
        Toast toast = null;
        View sViewWeakReference = createToastView();
        if (sViewWeakReference != null) {
            final View view = sViewWeakReference;
            //if (view != null) {
            //sToast = new Toast(Utils.getApp());
            if (sToastWeakReference == null) {
                sToastWeakReference = new WeakReference<>(new Toast(Utils.getApp()));
            }
            toast = sToastWeakReference.get();
            if (toast == null) {
                toast = new Toast(Utils.getApp());
                sToastWeakReference = new WeakReference<>(toast);
            }
            //sToast.setView(view);
            //sToast.setDuration(duration);
            toast.setView(view);
            toast.setDuration(duration);
            isCustom = true;
            //}
        }
        if (!isCustom) {
            if (messageColor != DEFAULT_COLOR) {
                SpannableString spannableString = new SpannableString(text);
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(messageColor);
                spannableString.setSpan(colorSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //sToast = Toast.makeText(Utils.getApp(), spannableString, duration);
                toast = Toast.makeText(Utils.getApp(), spannableString, duration);
            } else {
                //sToast = Toast.makeText(Utils.getApp(), text, duration);
                toast = Toast.makeText(Utils.getApp(), text, duration);
            }
        }
        View view = toast.getView();
        if (view instanceof LinearLayout) {
            try {
                TextView v = (TextView) ((LinearLayout) view).getChildAt(0);
                v.setGravity(Gravity.CENTER);
                v.setText(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (bgResource != -1) {
            view.setBackgroundResource(bgResource);
        } else if (backgroundColor != DEFAULT_COLOR) {
            view.setBackgroundColor(backgroundColor);
        }
        toast.setGravity(gravity, xOffset, yOffset);
        toast.show();
    }

    /**
     * 取消吐司显示
     */
    public static void cancel() {
        /*if (sToast != null) {
            sToast.cancel();
            sToast = null;
        }*/

        if (sToastWeakReference != null) {
            Toast t = sToastWeakReference.get();
            if (t != null) {
                t.cancel();
                t = null;
                sToastWeakReference.clear();
            }

        }
    }

    public static void showShortCenter(CharSequence toastStr) {
        Toast toast = Toast.makeText(Utils.getApp(), toastStr.toString(), Toast.LENGTH_SHORT);
        try {
            int tvToastId = Resources.getSystem().getIdentifier("message", "id", "android");
            TextView tvToast = toast.getView().findViewById(tvToastId);
            if (tvToast != null) {
                tvToast.setGravity(Gravity.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        toast.show();
    }
}