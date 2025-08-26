package com.allo.utils

import android.util.ArrayMap

object StringUtils {
    /**
     * 获取维语首字母
     */

    val CHINESE_LETTERS =            listOf("#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    val UY_LETTERS =                 listOf("ئا","ئە","ب","پ","ت","ج","چ","خ","د","ر","ز","ژ","س","ش","غ","ف","ق","ك","گ","ڭ","ل","م","ن","ھ","ئو","ئۇ","ئۆ","ئۈ","ۋ","ئې","ئى","ي")
    val UY_LETTERS_KEYBOARD_SHORT =  listOf("ئا","ئە","ب","س","د","ئې","ف","گ","غ","خ","ھ","ئى","ج","ك","ق","ل","م","ن","ئو","ڭ","پ","چ","ر","ژ","ت","ئۇ","ئۆ","ئۈ","ۋ","ش","ي","ز").reversed()
    val UY_LETTERS_KEYBOARD_SHORT_FULL =  listOf("ئا","ا","ئە","ە","ب","س","د","ئې","ې","ف","گ","غ","خ","ھ","ئى","ى","ج","ك","ق","ل","م","ن","ئو","و","ڭ","پ","چ","ر","ژ","ت","ئۇ","ۇ","ئۆ","ۆ","ئۈ","ۈ","ۋ","ش","ي","ز")
    val UY_LETTERS_SORT = ArrayMap<CharSequence,Int>()
    val SPECIAL_LETTERS = listOf('ا', 'ە', 'و', 'ۇ', 'ۆ', 'ۈ', 'ى', 'ې')

    init {
        UY_LETTERS_KEYBOARD_SHORT_FULL.forEachIndexed { index, letter ->
            UY_LETTERS_SORT[letter] = index
        }
    }
    const val SPECIAL_FIRST_LETTER = 'ئ'

    fun getUyghurFirst(input: String?): String {
        if (input.isNullOrEmpty()) return ""
        val second = input.second()
        if (input.startsWith(SPECIAL_FIRST_LETTER) && SPECIAL_LETTERS.contains(second)){
            return input.first().toString().plus(second)
        }
        return input.first().toString()
    }




    fun specialLetters() = SPECIAL_LETTERS
    fun chineseLetters() = CHINESE_LETTERS
    fun uyLetters() = UY_LETTERS
    fun uyLettersLetterShort() = UY_LETTERS_KEYBOARD_SHORT

    fun compareUyLetters(left:CharSequence,right:CharSequence):Int{
        val l = UY_LETTERS_SORT[left] ?: return 0
        val r = UY_LETTERS_SORT[right] ?: return 0
        return l.compareTo(r)
    }

    fun isUyChar(c:Char ):Boolean {
        if (c>= 1574.toChar() && c<= 1749.toChar()) {
            return true
        }
        return false
    }
}


fun String.second():Char?{
    return this.getOrNull(1)
}