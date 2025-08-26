package com.allo.musicplayer

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.allo.musicplayer.musicplayer.IMusic
import com.allo.musicplayer.musicplayer.IPlayerListener
import com.allo.musicplayer.musicplayer.OnBindCallBack

/**
 * @Author yforyoung
 * @Date 2021/12/28 17:55
 * @Desc
 */
class MusicPlayerController private constructor() {

    companion object {

        val INSTANCE: MusicPlayerController by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MusicPlayerController()
        }
    }

    private val TAG = "yforyoung"


    // binder
    private var mBinder: MusicPlayerService.MusicBinder? = null

    // conn
    private val conn: MusicServiceConnection by lazy {
        return@lazy MusicServiceConnection()
    }

    // 绑定回调
    private val callbackList = mutableListOf<OnBindCallBack>()

    fun bind(activity: Activity, service: Class<*>) {
        activity.bindService(
            Intent(activity, service),
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

    fun addBindCallback(onBindCallBack: OnBindCallBack) {
        if (callbackList.contains(onBindCallBack)) return
        callbackList.add(onBindCallBack)
    }

    fun removeBindCallback(onBindCallBack: OnBindCallBack) {
        if (callbackList.contains(onBindCallBack)) {
            callbackList.remove(onBindCallBack)
        }
    }

    fun play(position: Int, list: List<IMusic>) {
        if (position < 0) {
            return
        }
        try {
            mBinder?.player?.play(position, list)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun play(music: IMusic) {
        mBinder?.player?.play(music)
    }

    fun play() {
        mBinder?.player?.play()
    }

    fun nextPlay(music: IMusic) {
        mBinder?.player?.nextPlay(music)
    }

    fun nextPlay(list: List<IMusic>) {
        mBinder?.player?.nextPlay(list)
    }

    fun pause() {
        mBinder?.player?.pause()
    }

    fun next() {
        mBinder?.player?.next()
    }

    fun prev() {
        mBinder?.player?.prev()
    }

    fun addPlayerListener(listener: IPlayerListener) {
        mBinder?.player?.addPlayerListener(listener)
    }

    fun removePlayerListener(listener: IPlayerListener) {
        mBinder?.player?.removePlayerListener(listener)
    }

    fun isPlaying(): Boolean {
        return mBinder?.player?.isPlaying() == true
    }

    fun setLoopType(type: MusicLoopState?) {
        mBinder?.player?.setLoopType(type)
    }

    fun getPlayerList(): List<IMusic> {
        return mBinder?.player?.getPlayerList() ?: listOf()
    }

    fun getCurrent(): IMusic? {
        return mBinder?.player?.getCurrent()
    }

    fun getCurrentPosition(): Int {
        return mBinder?.player?.getCurrentPosition() ?: -1
    }

    fun getLoopPlayerList(): List<IMusic> {
        return mBinder?.player?.getLoopPlayerList() ?: listOf()
    }

    fun getLoopCurrentPosition(): Int {
        return mBinder?.player?.getLoopCurrentPosition() ?: -1
    }


    fun remove(music: IMusic) {
        mBinder?.player?.remove(music)
    }

    fun removeAll() {
        mBinder?.player?.removeAll()
    }

    fun getLoopState(): MusicLoopState {
        return mBinder?.player?.getLoopState() ?: MusicLoopState.SEQUENCE
    }


    fun getCurrentProgress(): Int {
        return mBinder?.player?.getCurrentDuration() ?: 0
    }

    fun getDuration(): Int {
        return mBinder?.player?.getDuration() ?: 0
    }

    fun release() {
        mBinder = null
    }


    inner class MusicServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mBinder = service as MusicPlayerService.MusicBinder?
            mBinder?.let {
                Log.i(TAG, "onServiceConnected: 服务绑定")
                if (callbackList.isNotEmpty()) {
                    callbackList.forEach {
                        it.onBind()
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBinder = null
            Log.i(TAG, "onServiceDisconnected: 服务解绑")
        }

    }

}