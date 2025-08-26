package com.allo.musicplayer

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.media.AudioAttributes
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.allo.musicplayer.musicplayer.IMusic
import com.allo.musicplayer.musicplayer.IMusicPlayer
import com.allo.musicplayer.musicplayer.IPlayerListener

/**
 * @Author yforyoung
 * @Date 2021/12/27 15:52
 * @Desc
 */
open class MusicPlayerService : Service() {
    companion object {
        private lateinit var binder: MusicBinder

        const val MUSIC_CHANNEL_ID = "com.allo.contacts.music"
        const val MUSIC_CHANNEL_NAME = "铃声播放"
        const val MUSIC_NOTICE_ID = 1917

        const val ACTION_NEXT = "allo.music.next"
        const val ACTION_PREV = "allo.music.prev"
        const val ACTION_PLAY = "allo.music.play"
        const val ACTION_CLOSE = "allo.music.close"
        const val ACTION_ROOT = "allo.music.root"

    }

    private val TAG = "yforyoung"

    // 状态
    private var mState: MusicState = MusicState.EMPTY

    // 通知栏的音乐封面
    private var currentCover: Bitmap? = null

    // 用于通知栏控制
    private val mReceiver: MusicNotifyBroadcastReceiver by lazy {
        return@lazy MusicNotifyBroadcastReceiver()
    }

    override fun onCreate() {
        super.onCreate()
        binder = getBinder()

        val filter = IntentFilter()
        filter.addAction(ACTION_NEXT)
        filter.addAction(ACTION_PLAY)
        filter.addAction(ACTION_PREV)
        filter.addAction(ACTION_ROOT)
        filter.addAction(ACTION_CLOSE)
        registerReceiver(getReceiver(), filter)
        createChannel()
    }

    open fun getReceiver(): MusicNotifyBroadcastReceiver {
        return mReceiver
    }

    open fun getBinder(): MusicBinder {
        return MusicBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: 启动")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.i(TAG, "onBind: 绑定")
        return binder
    }

    override fun onDestroy() {
        binder.player.release()
        unregisterReceiver(getReceiver())
        MusicPlayerController.INSTANCE.release()
        super.onDestroy()
        Log.i(TAG, "onDestroy:  服务销毁")

    }


    open fun getRemoteLayout(): Int {
        return R.layout.layout_remote_notify
    }

    open fun getPlayIcon(playing: Boolean): Int {
        return R.drawable.icon_logo
    }

    open fun getRootAction(): PendingIntent {
        return PendingIntent.getBroadcast(this, 0, Intent(ACTION_ROOT), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun getRemoteViews(title: String, author: String, cover: Bitmap?): RemoteViews {

        val remoteViews = RemoteViews(packageName, getRemoteLayout())

        val close = PendingIntent.getBroadcast(this, 0, Intent(ACTION_CLOSE), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val next = PendingIntent.getBroadcast(this, 0, Intent(ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val previous = PendingIntent.getBroadcast(this, 0, Intent(ACTION_PREV), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val play = PendingIntent.getBroadcast(this, 0, Intent(ACTION_PLAY), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//        val root = getRootAction()

        remoteViews.setOnClickPendingIntent(R.id.iv_close, close)
        remoteViews.setOnClickPendingIntent(R.id.iv_next, next)
        remoteViews.setOnClickPendingIntent(R.id.iv_prev, previous)
        remoteViews.setOnClickPendingIntent(R.id.iv_start, play)
//        remoteViews.setOnClickPendingIntent(R.id.root, root)

        remoteViews.setTextViewText(R.id.tv_title, title)
        remoteViews.setTextViewText(R.id.tv_author, author)
        if (cover == null) {
            remoteViews.setImageViewResource(R.id.iv_cover, getCoverPlaceHolder())
        } else {
            remoteViews.setImageViewBitmap(R.id.iv_cover, cover)
        }
        remoteViews.setImageViewResource(R.id.iv_start, getPlayIcon(mState == MusicState.PLAYING))
        if (author.isEmpty()) {
            remoteViews.setViewVisibility(R.id.tv_author, View.GONE)
        } else {
            remoteViews.setViewVisibility(R.id.tv_author, View.VISIBLE)
        }
        return remoteViews
    }

    open fun getCoverPlaceHolder(): Int {
        return R.drawable.icon_logo
    }

    protected fun sendNotify(title: String, author: String, cover: Bitmap?) {
        try {
            if (cover != null) currentCover = cover
            val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(this, MUSIC_CHANNEL_ID)
            } else {
                Notification.Builder(this)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setTicker("Nature")
                    .setSmallIcon(R.drawable.icon_logo)
                    .setContentTitle(title)
                    .setContentText(title)
                    .setAutoCancel(true)
                    .setContentIntent(getRootAction())
                    .setCustomContentView(getRemoteViews(title, author, cover))
            } else {
                builder.setTicker("Nature")
                    .setSmallIcon(R.drawable.icon_logo)
                    .setContentTitle(title)
                    .setContentText(title)
                    .setAutoCancel(true)
                    .setContentIntent(getRootAction())
                    .setContent(getRemoteViews(title, author, cover))
            }

            startForeground(MUSIC_NOTICE_ID, builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager.getNotificationChannel(MUSIC_CHANNEL_ID) == null) {
                val notificationChannel =
                    NotificationChannel(
                        MUSIC_CHANNEL_ID,
                        MUSIC_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_LOW
                    )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.setShowBadge(true)
                notificationChannel.setSound(
                    null, AudioAttributes.Builder().setUsage(
                        AudioAttributes.USAGE_NOTIFICATION
                    ).build()
                )
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }

    open inner class MusicBinder : Binder(), IPlayerListener {
        val player: IMusicPlayer by lazy {
            return@lazy MusicPlayer().apply {
                addPlayerListener(this@MusicBinder)
                enableAudioFocus(true,this@MusicPlayerService)
            }
        }

        override fun onUpdate(music: IMusic, progress: Int, cachedProgress: Int, duration: Int) {

        }

        // 重写这个方法 用来刷新cover
        override fun onPlayingStateChanged(music: IMusic, state: MusicState) {
            this@MusicPlayerService.mState = state
            // 切歌的时候重置cover
            if (state == MusicState.PREPARING) {
                currentCover = null
            }
            sendNotify(music.getTitle(), music.getAuthor(), currentCover)
        }

        override fun onInfo(code: Int, data: Any) {

        }
    }


    open inner class MusicNotifyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_NEXT -> {
                    actionNext()
                }
                ACTION_PLAY -> {
                    actionPlay()
                }
                ACTION_PREV -> {
                    actionPrev()
                }
                ACTION_CLOSE -> {
                    actionClose()
                }
                ACTION_ROOT -> {
                    // 跳转
                    actionRoot()
                }
            }
        }

        open fun actionNext(){
            binder.player.next()
        }

        open fun actionPrev(){
            binder.player.prev()
        }

        open fun actionPlay(){
            if (mState == MusicState.PLAYING) {
                binder.player.pause()
            } else {
                binder.player.play()
            }
        }

        open fun actionClose(){
            // 停止播放
            binder.player.pause()
            // 关闭通知栏
            stopForeground(true)
        }

        open fun actionRoot() {
            Log.i(TAG, "actionRoot: ")
        }

    }

}