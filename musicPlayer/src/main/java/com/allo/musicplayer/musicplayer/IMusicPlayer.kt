package com.allo.musicplayer.musicplayer

import android.content.Context
import com.allo.musicplayer.MusicLoopState

/**
 * @Author yforyoung
 * @Date 2021/12/27 16:21
 * @Desc
 */
interface IMusicPlayer {

    // 开启焦点互斥
    fun enableAudioFocus(enable: Boolean, c: Context)

    // 播放音乐  重置音乐列表
    fun play(position: Int, list: List<IMusic>)

    // 播放音乐 如果列表中没有这首歌  重置列表
    fun play(music: IMusic)

    // 播放当前音乐/resume
    fun play()

    // 添加到下一首播放
    fun nextPlay(music: IMusic)

    // 添加到下一首播放
    fun nextPlay(list: List<IMusic>)

    // 如果存在  从列表中移除  正在播放的话切到下一首继续播放
    fun remove(music: IMusic)

    // 清除整个歌单
    fun removeAll()

    // 暂停
    fun pause()

    // 下一首
    fun next()

    // 上一首
    fun prev()

    fun getCurrentDuration(): Int

    fun getDuration(): Int

    fun addPlayerListener(listener: IPlayerListener)

    fun removePlayerListener(listener: IPlayerListener)

    fun isPlaying(): Boolean

    // 释放   释放后不可再播放  因此需要在整个服务销毁时才调用
    fun release()

    // 循环模式
    fun setLoopType(type: MusicLoopState?)

    // 获取当前播放列表
    fun getPlayerList(): List<IMusic>

    // 获取当前播放的音乐
    fun getCurrent(): IMusic?

    // 当前播放的位置
    fun getCurrentPosition(): Int

    // 不同循环方式返回的数组不同
    fun getLoopPlayerList(): List<IMusic>

    fun getLoopCurrentPosition(): Int

    // 获取循环方式
    fun getLoopState(): MusicLoopState

}