package com.allo.utils;


import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * 手势utils
 *
 * @author: ifan
 * @date: 8/13/21
 */
class GestureUtils {
    /**
     * android 4.4或者emui3状态栏ID识位
     */
//    static final int IMMERSION_STATUS_BAR_VIEW_ID = R.id.immersion_status_bar_view;
    /**
     * android 4.4或者emui3导航栏ID识位
     */
//    static final int IMMERSION_NAVIGATION_BAR_VIEW_ID = R.id.immersion_navigation_bar_view;
    /**
     * 状态栏高度标识位
     */
    static final String IMMERSION_STATUS_BAR_HEIGHT = "status_bar_height";
    /**
     * 导航栏竖屏高度标识位
     */
    static final String IMMERSION_NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    /**
     * 导航栏横屏高度标识位
     */
    static final String IMMERSION_NAVIGATION_BAR_HEIGHT_LANDSCAPE = "navigation_bar_height_landscape";
    /**
     * 导航栏宽度标识位位
     */
    static final String IMMERSION_NAVIGATION_BAR_WIDTH = "navigation_bar_width";
    /**
     * 小米导航栏显示隐藏标识位 0-三按钮导航，1-手势导航
     */
    static final String IMMERSION_NAVIGATION_BAR_MODE_MIUI = "force_fsg_nav_bar";
    /**
     * 小米导航栏手势导航情况下，是否隐藏手势提示线,0：显示 1：隐藏
     */
    static final String IMMERSION_NAVIGATION_BAR_MODE_MIUI_HIDE = "hide_gesture_line";
    /**
     * 华为导航栏显示隐藏标识位 0-三按钮导航，1-手势导航
     */
    static final String IMMERSION_NAVIGATION_BAR_MODE_EMUI = "navigationbar_is_min";
    /**
     * VIVO导航栏显示隐藏标识位 0-三按钮导航，1-经典三段式，2-全屏手势
     */
    static final String IMMERSION_NAVIGATION_BAR_MODE_VIVO = "navigation_gesture_on";
    /**
     * OPPO导航栏显示隐藏标识位 0-三按钮导航，1-手势导航，2-上划手势，3-侧滑手势
     */
    static final String IMMERSION_NAVIGATION_BAR_MODE_OPPO = "hide_navigationbar_enable";
    /**
     * SAMSUNG导航栏显示隐藏标识位 0-三按钮导航 1-手势导航
     */
    static final String IMMERSION_NAVIGATION_BAR_MODE_SAMSUNG = "navigation_bar_gesture_while_hidden";
    /**
     * 三星导航栏手势导航情况下,手势类型 0：三段式线条 1：单线条
     */
    static final String IMMERSION_NAVIGATION_BAR_MODE_SAMSUNG_GESTURE_TYPE = "navigation_bar_gesture_detail_type";
    /**
     * 三星导航栏手势导航情况下，是否隐藏手势提示线,0：隐藏 1：显示
     */
    static final String IMMERSION_NAVIGATION_BAR_MODE_SAMSUNG_GESTURE = "navigation_bar_gesture_hint";
    /**
     * SAMSUNG导航栏显示隐藏标识位 0-三按钮导航，1-手势导航
     */
    static final String IMMERSION_NAVIGATION_BAR_MODE_SAMSUNG_OLD = "navigationbar_hide_bar_enabled";
    /**
     * 默认手势导航 0-三按钮导航，1-双按钮导航，2-手势导航
     */
    static final String IMMERSION_NAVIGATION_BAR_MODE_DEFAULT = "navigation_mode";
    /**
     * MIUI状态栏字体黑色与白色标识位
     */
    static final String IMMERSION_STATUS_BAR_DARK_MIUI = "EXTRA_FLAG_STATUS_BAR_DARK_MODE";
    /**
     * MIUI导航栏图标黑色与白色标识位
     */
    static final String IMMERSION_NAVIGATION_BAR_DARK_MIUI = "EXTRA_FLAG_NAVIGATION_BAR_DARK_MODE";

    /**
     * 自动改变字体颜色的临界值标识位
     */
    static final int IMMERSION_BOUNDARY_COLOR = 0xFFBABABA;

    /**
     * 修复状态栏与布局重叠标识位，默认不修复
     */
    static final int FLAG_FITS_DEFAULT = 0X00;
    /**
     * 修复状态栏与布局重叠标识位，使用titleBar方法修复
     */
    static final int FLAG_FITS_TITLE = 0X01;
    /**
     * 修复状态栏与布局重叠标识位，使用titleBarMarginTop方法修复
     */
    static final int FLAG_FITS_TITLE_MARGIN_TOP = 0X02;
    /**
     * 修复状态栏与布局重叠标识位，使用StatusBarView方法修复
     */
    static final int FLAG_FITS_STATUS = 0X03;
    /**
     * 修复状态栏与布局重叠标识位，使用fitsSystemWindows方法修复
     */
    static final int FLAG_FITS_SYSTEM_WINDOWS = 0X04;

    /**
     * 获取全面屏相关信息
     *
     * @param context Context
     * @return FullScreenBean
     */
    public static GestureBean getGestureBean(Context context) {
        GestureBean gestureBean = new GestureBean();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && context != null && context.getContentResolver() != null) {
            ContentResolver contentResolver = context.getContentResolver();
            NavigationBarType navigationBarType = NavigationBarType.UNKNOWN;
            int type = -1;
            boolean isGesture = false;
            boolean checkNavigation = false;
            if (OSUtils.isHuaWei() || OSUtils.isEMUI()) {
                if (OSUtils.isEMUI3_x() || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    type = Settings.System.getInt(contentResolver, IMMERSION_NAVIGATION_BAR_MODE_EMUI, -1);
                } else {
                    type = Settings.Global.getInt(contentResolver, IMMERSION_NAVIGATION_BAR_MODE_EMUI, -1);
                }
                if (type == 0) {
                    navigationBarType = NavigationBarType.CLASSIC;
                    isGesture = false;
                } else if (type == 1) {
                    navigationBarType = NavigationBarType.GESTURES;
                    isGesture = true;
                }
            } else if (OSUtils.isXiaoMi() || OSUtils.isMIUI()) {
                type = Settings.Global.getInt(contentResolver, IMMERSION_NAVIGATION_BAR_MODE_MIUI, -1);
                if (type == 0) {
                    navigationBarType = NavigationBarType.CLASSIC;
                    isGesture = false;
                } else if (type == 1) {
                    navigationBarType = NavigationBarType.GESTURES;
                    isGesture = true;
                    int i = Settings.Global.getInt(contentResolver, IMMERSION_NAVIGATION_BAR_MODE_MIUI_HIDE, -1);
                    checkNavigation = i != 1;
                }
            } else if (OSUtils.isVivo() || OSUtils.isFuntouchOrOriginOs()) {
                type = Settings.Secure.getInt(contentResolver, IMMERSION_NAVIGATION_BAR_MODE_VIVO, -1);
                if (type == 0) {
                    navigationBarType = NavigationBarType.CLASSIC;
                    isGesture = false;
                } else if (type == 1) {
                    navigationBarType = NavigationBarType.GESTURES_THREE_STAGE;
                    isGesture = true;
                } else if (type == 2) {
                    navigationBarType = NavigationBarType.GESTURES;
                    isGesture = true;
                }
            } else if (OSUtils.isOppo() || OSUtils.isColorOs()) {
                type = Settings.Secure.getInt(contentResolver, IMMERSION_NAVIGATION_BAR_MODE_OPPO, -1);
                if (type == 0) {
                    navigationBarType = NavigationBarType.CLASSIC;
                    isGesture = false;
                } else if (type == 1 || type == 2 || type == 3) {
                    navigationBarType = NavigationBarType.GESTURES;
                    isGesture = true;
                }
            } else if (OSUtils.isSamsung()) {
                type = Settings.Global.getInt(contentResolver, IMMERSION_NAVIGATION_BAR_MODE_SAMSUNG, -1);
                if (type != -1) {
                    if (type == 0) {
                        navigationBarType = NavigationBarType.CLASSIC;
                        isGesture = false;
                    } else if (type == 1) {
                        isGesture = true;
                        int gestureType = Settings.Global.getInt(contentResolver, IMMERSION_NAVIGATION_BAR_MODE_SAMSUNG_GESTURE_TYPE, 1);
                        if (gestureType == 1) {
                            navigationBarType = NavigationBarType.GESTURES;
                        } else {
                            navigationBarType = NavigationBarType.GESTURES_THREE_STAGE;
                        }
                        int hide = Settings.Global.getInt(contentResolver, IMMERSION_NAVIGATION_BAR_MODE_SAMSUNG_GESTURE, 1);
                        checkNavigation = hide == 1;
                    }
                } else {
                    type = Settings.Global.getInt(contentResolver, IMMERSION_NAVIGATION_BAR_MODE_SAMSUNG_OLD, -1);
                    if (type == 0) {
                        navigationBarType = NavigationBarType.CLASSIC;
                        isGesture = false;
                    } else if (type == 1) {
                        navigationBarType = NavigationBarType.GESTURES;
                        isGesture = true;
                    }
                }
            }
            if (type == -1) {
                type = Settings.Secure.getInt(contentResolver, IMMERSION_NAVIGATION_BAR_MODE_DEFAULT, -1);
                if (type == 0) {
                    navigationBarType = NavigationBarType.CLASSIC;
                    isGesture = false;
                } else if (type == 1) {
                    navigationBarType = NavigationBarType.DOUBLE;
                    isGesture = false;
                } else if (type == 2) {
                    navigationBarType = NavigationBarType.GESTURES;
                    isGesture = true;
                    checkNavigation = true;
                }
            }
            gestureBean.isGesture = isGesture;
            gestureBean.checkNavigation = checkNavigation;
            gestureBean.type = navigationBarType;
        }
        return gestureBean;
    }

    static class GestureBean {
        /**
         * 是否有手势操作
         */
        public boolean isGesture = false;
        /**
         * 需要校验导航栏高度，需要检查的机型，小米，三星，原生
         */
        public boolean checkNavigation = false;
        /**
         * 手势类型
         */
        public NavigationBarType type;

        @Override
        public String toString() {
            return "GestureBean{" +
                    "isGesture=" + isGesture +
                    ", checkNavigation=" + checkNavigation +
                    ", type=" + type +
                    '}';
        }
    }
}
