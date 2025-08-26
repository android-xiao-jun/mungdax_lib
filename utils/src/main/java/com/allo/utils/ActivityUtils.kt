package com.allo.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build


class ActivityUtils private constructor() {
    companion object {
        /**
         * Return the name of launcher activity.
         *
         * @return the name of launcher activity
         */
        val launcherActivity: String
            get() = getLauncherActivity(Utils.getApp().packageName)

        /**
         * Return the name of launcher activity.
         *
         * @param pkg The name of the package.
         * @return the name of launcher activity
         */
        fun getLauncherActivity(pkg: String?): String {
            if (pkg.isNullOrBlank()) return ""
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setPackage(pkg)
            val pm = Utils.getApp().packageManager
            val info = pm.queryIntentActivities(intent, 0)
            return if (info.size == 0) {
                ""
            } else info[0].activityInfo.name
        }

        fun getActivityByContext(context: Context?): Activity? {
            if (context !is Activity) return null
            return if (!isActivityAlive(context)) null else context
        }

        fun isActivityAlive(context: Context?): Boolean {
            return isActivityAlive(getActivityByContext(context!!))
        }

        fun isActivityAlive(activity: Activity?): Boolean {
            return (activity != null && !activity.isFinishing
                    && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !activity.isDestroyed))
        }


    }


    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}