package com.allo.musicplayer.musicplayer

/**
 * @Author yforyoung
 * @Date 2021/12/27 16:17
 * @Desc
 */
interface IMusic {
    fun getTitle(): String

    fun getAuthor(): String

    fun getCover(): String

    fun getUrl(): String

    // 来源  模板/ 专题
    fun getSource(): Any

    fun equalsMusic(obj: Any): Boolean

}