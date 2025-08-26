package com.allo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UtilsBridge {

    static SPUtils getSpUtils4Utils() {
        return SPUtils.with("Utils");
    }

    static String getLocal(Context ctx, String key) {
        SharedPreferences prefs = ctx.getSharedPreferences("Utils", Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

}
