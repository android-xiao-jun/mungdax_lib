package com.allo.musicplayer.musicplayer

import com.allo.musicplayer.MusicState

/**
 * @Author yforyoung
 * @Date 2021/12/27 16:26
 * @Desc
 */
interface IPlayerListener {

    fun onUpdate(music: IMusic, progress: Int, cachedProgress: Int, duration: Int)

    fun onPlayingStateChanged(music: IMusic, state: MusicState)

    fun onInfo(code: Int, data: Any)

    companion object {
        const val DATA_CHANGED = 1  // 数据改变   添加或移除了数据
        const val LOOP_CHANGED = 2  // 循环方式改变
        const val DATA_EMPTY = 3    // 播放列表为空了
    }
}