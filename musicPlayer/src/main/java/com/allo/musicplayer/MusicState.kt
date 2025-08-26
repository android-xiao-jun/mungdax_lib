package com.allo.musicplayer

/**
 * @Author yforyoung
 * @Date 2021/12/27 16:29
 * @Desc
 */
sealed class MusicState {
    object EMPTY : MusicState()
    object PREPARING : MusicState()
    object PREPARED : MusicState()
    object PLAYING : MusicState()
    object PAUSED : MusicState()
    object COMPLETE : MusicState()
    object ERROR : MusicState()
}

sealed class MusicLoopState {
    object SINGLE : MusicLoopState()  //单曲循环
    object SEQUENCE : MusicLoopState() // 顺序播放
    object RANDOM : MusicLoopState() // 随机播放
}