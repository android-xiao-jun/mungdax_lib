package com.allo.utils;
import android.content.Context;
import android.provider.Settings;

public class AndroidIdUtils {
    //在 Android 上获取 Android ID 可以使用以下代码：
    public static String getAndroidId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }catch (Exception e){
            e.printStackTrace();
        }
        return SPUtils.with().getString("key_device_id");
    }
}