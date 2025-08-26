package com.allo.view

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData

/**
 * @Author zhang
 * @Date 2021/12/28 17:55
 * @Desc
 */
class NetStatesController private constructor() {

    companion object {

        val INSTANCE: NetStatesController by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetStatesController()
        }
    }
    private val TAG = "===z"


    // binder
    private var mBinder: NetStatesService.NetStateBinder? = null

    val netConnect = MutableLiveData(true)


    // conn
    private val conn: NetStateServiceConnection by lazy {
        return@lazy NetStateServiceConnection()
    }


    fun bind(activity: Activity) {
        activity.bindService(
            Intent(activity, NetStatesService::class.java),
            conn,
            Context.BIND_AUTO_CREATE
        )
    }

    fun unBind(activity: Activity) {
        mBinder?.let {
            kotlin.runCatching {
                activity.unbindService(conn)
            }
        }
    }

    inner class NetStateServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mBinder = service as NetStatesService.NetStateBinder?
            mBinder?.let {
                Log.e(TAG, "onServiceConnected: 服务绑定")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBinder = null
            Log.e(TAG, "onServiceDisconnected: 服务解绑")
        }
    }

}