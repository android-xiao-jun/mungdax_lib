package com.allo.view

import android.app.*
import android.content.*
import android.net.ConnectivityManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.allo.utils.JobScheduler
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * @Author zhang
 * @Date 2021/12/27 15:52
 * @Desc
 */
open class NetStatesService : Service() {

    private val TAG = "===z"

    companion object {
        const val KEY_GLOBAL_NET_STATE = "key_global_net_state"
        private lateinit var binder: NetStateBinder
    }


    private val mReceiver: NetStateBroadcastReceiver by lazy {
        return@lazy NetStateBroadcastReceiver()
    }

    override fun onCreate() {
        super.onCreate()
        initReceiver()
    }

    private fun initReceiver() {
        binder = getBinder()
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(getReceiver(), filter)
    }

    open fun getBinder(): NetStateBinder {
        return NetStateBinder()
    }

    open fun getReceiver(): NetStateBroadcastReceiver {
        return mReceiver
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand: 启动")
        initReceiver()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        unregisterReceiver(getReceiver())
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    open inner class NetStateBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (ConnectivityManager.CONNECTIVITY_ACTION == intent?.action) {
                val cm =
                    context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = cm.activeNetworkInfo
                if (null != activeNetwork) {
                    when (activeNetwork.type) {
                        ConnectivityManager.TYPE_WIFI -> {
                            Log.e(TAG, "wifi 连接")
                            setNetConnect(true)
                        }
                        ConnectivityManager.TYPE_MOBILE -> {
                            Log.e(TAG, "mobile 连接")
                            setNetConnect(true)
                        }
                        else -> {
                            Log.e(TAG, "网络断开")
                            JobScheduler.uiJobDelay({
                                if (isNetworkAvailable(context)) {
                                    setNetConnect(true)
                                } else {
                                    setNetConnect(false)
                                }
                            },600)
                        }
                    }
                } else {
                    Log.e(TAG, "网络断开")
                    JobScheduler.uiJobDelay({
                        if (isNetworkAvailable(context)) {
                            setNetConnect(true)
                        } else {
                            setNetConnect(false)
                        }
                    },600)
                }
            }
        }

        fun setNetConnect(connect: Boolean) {
            LiveEventBus.get(KEY_GLOBAL_NET_STATE).post(connect)
        }
    }

    open fun isNetworkAvailable(context: Context): Boolean {
        val manager = context.applicationContext.getSystemService(
            CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val info = manager.activeNetworkInfo
        return !(null == info || !info.isAvailable)
    }

    open inner class NetStateBinder : Binder()

}