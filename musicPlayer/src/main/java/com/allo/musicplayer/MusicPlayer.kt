package com.allo.musicplayer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.allo.musicplayer.musicplayer.IMusic
import com.allo.musicplayer.musicplayer.IMusicPlayer
import com.allo.musicplayer.musicplayer.IPlayerListener
import com.allo.utils.JobScheduler
import java.util.*

/**
 * @Author yforyoung
 * @Date 2021/12/27 16:16
 * @Desc
 */
class MusicPlayer : IMusicPlayer, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
    MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {
    private val TAG = "MusicPlayer"

    // 播放器 不对外暴露
    private val player: MediaPlayer by lazy {
        return@lazy MediaPlayer().apply {
            this.setOnPreparedListener(this@MusicPlayer)
            this.setOnErrorListener(this@MusicPlayer)
            this.setOnCompletionListener(this@MusicPlayer)
            this.setOnBufferingUpdateListener(this@MusicPlayer)
        }
    }

    // 监听列表
    private val listenerList = mutableListOf<IPlayerListener>()

    // 随机播放的数据
    private val randomList = mutableListOf<IMusic>()

    // 播放列表
    private val musicList = mutableListOf<IMusic>()

    // 当前播放歌曲位置
    private var current = -1

    // 当前播放状态
    private var state: MusicState = MusicState.EMPTY

    // 循环方式
    private var loopState: MusicLoopState = MusicLoopState.SEQUENCE

    // 缓存进度  百分比
    private var cachedProgress: Int = 0

    // 当前进度  百分比
    private var currentProgress: Int = 0

    // 总进度   秒
    private var duration: Int = 0

    private var audioFocus = false

    private lateinit var audioManager: AudioManager


    private lateinit var focusRequest: AudioFocusRequest

    override fun enableAudioFocus(enable: Boolean, c: Context) {
        if (enable) {
            audioManager = c.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(this)
                    .setWillPauseWhenDucked(false)
                    .setAcceptsDelayedFocusGain(true)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .build()
            }
        }
        audioFocus = enable
    }


    override fun play(position: Int, list: List<IMusic>) {
        Log.i(TAG, "play: 更新列表的播放： $position")
        // 清空状态
        resetPlayer()
        // 重置歌曲列表
        musicList.clear()
        musicList.addAll(list)
        if (randomList.isNotEmpty()) {
            randomList.clear()
            randomList.addAll(list.shuffled())
        }
        // 播放歌曲    可能抛出数组越界异常  应该在外部去判断越界
        current = position
        playCurrent()
    }

    // 如果播放列表中有这首歌  直接播放  如果没有  替换整个列表
    override fun play(music: IMusic) {
        Log.i(TAG, "play: 播放这首歌  ${music.getTitle()}")
        resetPlayer()
        if (musicList.contains(music)) {
            current = musicList.indexOf(music)
            playCurrent()
        } else {
            // 重置歌曲列表
            musicList.clear()
            musicList.add(music)
            current = 0
            // 播放歌曲
            playCurrent()
        }
    }

    // 如果歌曲暂停 resume   否则播放current
    override fun play() {
        if (state == MusicState.PAUSED) {
            if (audioFocus) {
                changeAudioFocus(AudioManager.AUDIOFOCUS_GAIN)
            }
            // 开始播放
            player.start()
            portStateChanged(MusicState.PLAYING)
            interval(true)
        } else {
            resetPlayer()
            playCurrent()
        }
    }


    // 添加到列表中 下一首播放
    override fun nextPlay(music: IMusic) {
        val p = if (musicList.isEmpty()) {
            current = 0
            0
        } else {
            current + 1
        }
        kotlin.runCatching {
            musicList.add(p, music)
            if (randomList.isNotEmpty()) {
                randomList.add(getLoopCurrentPosition() + 1, music)     // 随机列表也加入下一首
            } else {
                randomList.add(music)
            }
            // 暂停情况下继续暂停
            portInfo(IPlayerListener.DATA_CHANGED, "")
        }
    }

    // 添加到列表中 下一首播放
    override fun nextPlay(list: List<IMusic>) {
        val p = if (musicList.isEmpty()) {
            current = 0
            0
        } else {
            current + 1
        }
        musicList.addAll(p, list)
        if (randomList.isNotEmpty()) {
            randomList.addAll(list) // 加入新的列表
            randomList.shuffle()    //  然后乱序
            randomList.remove(list[0])  // 乱序后移除新增列表第一条数据
            randomList.add(getLoopCurrentPosition() + 1, list[0])   // 将新列表第一条数据插入正在播放的歌曲后面
        } else {
            randomList.addAll(musicList.shuffled())
        }
        // 暂停情况下继续暂停
        portInfo(IPlayerListener.DATA_CHANGED, "")

    }

    override fun remove(music: IMusic) {
        val removePosition = musicList.indexOf(music)

        if (removePosition < current) {
            current--
        } else if (removePosition == current) {
            if (isPlaying()) {
                // 有更多的歌曲可以播放  则播放下一首
                if (current >= musicList.size - 1) {
                    next()
                } else {
                    resetPlayer()
                    playCurrent()
                }
            } else {
                // 切换到0
                if (current >= musicList.size - 1) {
                    current = 0
                }
            }
        }

        // 移除歌曲
        if (musicList.contains(music)) {
            musicList.remove(music)
        }
        if (randomList.contains(music)) {
            randomList.remove(music)
        }

        if (musicList.isEmpty()) {
            resetPlayer()
            setLoopType(MusicLoopState.SEQUENCE)
            portInfo(IPlayerListener.DATA_EMPTY, "")
        } else {
            portInfo(IPlayerListener.DATA_CHANGED, "")
        }
    }

    override fun removeAll() {
        resetPlayer()
        musicList.clear()
        setLoopType(MusicLoopState.SEQUENCE)
        portInfo(IPlayerListener.DATA_EMPTY, "")
    }

    // 暂停
    override fun pause() {
        if (audioFocus) {
            // 主动暂停注销焦点监听 避免手动暂停后获得焦点导致恢复播放
            changeAudioFocus(AudioManager.AUDIOFOCUS_LOSS)
        }
        kotlin.runCatching {
            if (player.isPlaying) {
                player.pause()
            }
        }
        interval(false)
        portStateChanged(MusicState.PAUSED)

    }

    /**
     *  播放时注册  销毁时移除
     * */
    private fun changeAudioFocus(focusType: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (focusType == AudioManager.AUDIOFOCUS_LOSS) {
                audioManager.abandonAudioFocusRequest(focusRequest)
            } else {
                audioManager.requestAudioFocus(focusRequest)
            }
        } else {
            if (focusType == AudioManager.AUDIOFOCUS_LOSS) {
                audioManager.abandonAudioFocus(this)
            } else {
                audioManager.requestAudioFocus(
                    this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_LOSS
                )
            }

        }
    }

    override fun next() {
        Log.i(TAG, "next: $current")
        resetPlayer()
        when (loopState) {
            MusicLoopState.RANDOM -> {
                // 随机列表中的position
                var rCurrent = randomList.indexOf(musicList[current])
                if (rCurrent >= randomList.size - 1) {
                    rCurrent = 0
                } else {
                    rCurrent++
                }
                current = musicList.indexOf(randomList[rCurrent])
                playCurrent()
            }
            MusicLoopState.SEQUENCE, MusicLoopState.SINGLE -> {
                if (current >= musicList.size - 1) {
                    current = 0
                } else {
                    current++
                }
                playCurrent()
            }
        }
    }

    override fun prev() {
        resetPlayer()
        when (loopState) {
            MusicLoopState.RANDOM -> {
                // 随机列表中的position
                var rCurrent = randomList.indexOf(musicList[current])
                if (rCurrent <= 0) {
                    rCurrent = randomList.size - 1
                } else {
                    rCurrent--
                }
                current = musicList.indexOf(randomList[rCurrent])
                playCurrent()
            }
            MusicLoopState.SEQUENCE, MusicLoopState.SINGLE -> {
                if (current <= 0) {
                    current = musicList.size - 1
                } else {
                    current--
                }
                playCurrent()
            }
        }
    }

    override fun getCurrentDuration(): Int {
        return try {
            player.currentPosition / 1000
        } catch (e: Exception) {
            0
        }
    }

    override fun getDuration(): Int {
        return try {
            player.duration / 1000
        } catch (e: Exception) {
            0
        }
    }

    override fun addPlayerListener(listener: IPlayerListener) {
        if (listenerList.contains(listener)) return
        listenerList.add(listener)
    }

    override fun removePlayerListener(listener: IPlayerListener) {
        if (listenerList.contains(listener)) {
            listenerList.remove(listener)
        }
    }

    override fun isPlaying(): Boolean {
        return player.isPlaying
    }

    override fun release() {
        kotlin.runCatching {    // 异常状态 catch掉
            clear()
            changeAudioFocus(AudioManager.AUDIOFOCUS_LOSS)
            player.stop()
            player.release()
        }
    }

    // 循环方式
    override fun setLoopType(type: MusicLoopState?) {
        loopState = type ?: when (loopState) {
            MusicLoopState.SEQUENCE -> {
                MusicLoopState.RANDOM
            }
            MusicLoopState.RANDOM -> {
                MusicLoopState.SINGLE
            }
            else -> {
                MusicLoopState.SEQUENCE
            }
        }
        player.isLooping = loopState == MusicLoopState.SINGLE
        if (loopState == MusicLoopState.RANDOM) {
            if (randomList.isEmpty()) {
                randomList.addAll(musicList)
            }
            randomList.shuffle()    // 乱序
        }
        portInfo(IPlayerListener.LOOP_CHANGED, loopState)
    }

    override fun getPlayerList(): List<IMusic> {
        return musicList
    }

    override fun getCurrent(): IMusic? {
        if (current == -1 || musicList.size == 0) {
            return null
        }
        if (current > musicList.size - 1) {
            return musicList[0]
        } else {
            return musicList[current]
        }
    }

    override fun getCurrentPosition(): Int {
        return current
    }

    override fun getLoopPlayerList(): List<IMusic> {
        if (loopState == MusicLoopState.RANDOM) {
            return randomList
        } else {
            return musicList
        }
    }

    override fun getLoopCurrentPosition(): Int {
        if (loopState == MusicLoopState.RANDOM) {
            return randomList.indexOf(musicList[current])
        } else {
            return current
        }
    }

    override fun getLoopState(): MusicLoopState {
        return loopState
    }

    // 准备完毕
    override fun onPrepared(mp: MediaPlayer?) {
        portStateChanged(MusicState.PREPARED)

        if (audioFocus) {
            changeAudioFocus(AudioManager.AUDIOFOCUS_GAIN)
        }
        // 开始播放
        player.start()
        portStateChanged(MusicState.PLAYING)
        interval(true)

    }

    // 异常情况
    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        portStateChanged(MusicState.ERROR)
        clear()
        // 下一首
        if (what == -38) return true    // 可能是异常调用getDuration
        next()
        return true    //false  onCompleteListener 会被调用
    }

    // 播放完成
    override fun onCompletion(mp: MediaPlayer?) {
        portStateChanged(MusicState.COMPLETE)
        clear()
        // 下一首
        next()
    }

    // 缓存进度
    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        cachedProgress = percent
    }


    private fun playCurrent() {
        if (musicList.isNullOrEmpty())
            return

        kotlin.runCatching {
            player.setDataSource(musicList[current].getUrl())
            player.prepareAsync()
            portStateChanged(MusicState.PREPARING)
        }
    }

    // 必须用在playCurrent之前
    private fun resetPlayer() {
        portStateChanged(MusicState.EMPTY)
        clear()
        kotlin.runCatching {
            player.stop()
            player.reset()
        }
    }


    // 更新播放进度
    private fun portUpdate(progress: Int, cachedProgress: Int, duration: Int) {
        if (listenerList.isEmpty()) return
        kotlin.runCatching {
            val music = if (current >= 0 && current < musicList.size) {
                musicList[current]
            } else {
                return
            }
            listenerList.forEach {
                it.onUpdate(music, progress, cachedProgress, duration)
            }
        }
    }


    // 更新播放状态
    private fun portStateChanged(state: MusicState) {
        this.state = state
        if (listenerList.isEmpty()) return
        kotlin.runCatching {
            val music = if (current >= 0 && current < musicList.size) {
                musicList[current]
            } else {
                return
            }
            listenerList.forEach {
                it.onPlayingStateChanged(music, state)
            }
        }
    }

    private fun portInfo(code: Int, data: Any) {
        if (listenerList.isEmpty()) return
        kotlin.runCatching {
            listenerList.forEach {
                it.onInfo(code, data)
            }
        }
    }


    // 清除
    private fun clear() {

        interval(false)
        cachedProgress = 0
        currentProgress = 0
        duration = 0

    }

    private var _interval = false

    // 开始轮询
    private fun interval(start: Boolean) {
        if (start) {
            _interval = true
            intervalProgress()
        } else {
            _interval = false
        }
    }

    //
    private fun intervalProgress() {
        Thread {
            while (_interval) {
                // 有进度
                if (player.currentPosition > 0 && player.duration > 0) {
                    currentProgress =
                        ((player.currentPosition.toFloat() / player.duration) * 100).toInt()
                    duration = player.duration / 1000
                    JobScheduler.uiJob {
                        portUpdate(currentProgress, cachedProgress, duration)
                    }
                }
                Thread.sleep(1000)
            }
        }.start()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS -> {
                kotlin.runCatching {
                    Log.i(TAG, "onAudioFocusChange: 失去焦点 暂停播放")
                    if (player.isPlaying) {
                        player.pause()
                        interval(false)
                        portStateChanged(MusicState.PAUSED)
                    }
                }
            }

            AudioManager.AUDIOFOCUS_GAIN -> {
                kotlin.runCatching {
                    Log.i(TAG, "onAudioFocusChange:获取到焦点  恢复播放")
                    // 开始播放
                    player.start()
                    portStateChanged(MusicState.PLAYING)
                    interval(true)
                }
            }
        }
    }

}