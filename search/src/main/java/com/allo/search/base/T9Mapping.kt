package com.allo.search.base

import android.util.ArrayMap
import com.allo.utils.LanguageUtils
import java.util.*

object T9Mapping {
    private val T9_MAPPING: MutableList<Map<String, List<String>>> = ArrayList()

    val mapping: List<Map<String, List<String>>> get() = T9_MAPPING

    val keyToNumMapping = ArrayMap<String,String>()

    init {
        val chineseMapping: MutableMap<String, List<String>> = mutableMapOf()
        chineseMapping["1"] = listOf()
        chineseMapping["2"] = listOf("A", "B", "C")
        chineseMapping["3"] = listOf("D", "E", "F")
        chineseMapping["4"] = listOf("G", "H", "I")
        chineseMapping["5"] = listOf("J", "K", "L")
        chineseMapping["6"] = listOf("M", "N", "O")
        chineseMapping["7"] = listOf("P", "Q", "R", "S")
        chineseMapping["8"] = listOf("T", "U", "V")
        chineseMapping["9"] = listOf("W", "X", "Y", "Z")
        chineseMapping["*"] = listOf()
        chineseMapping["0"] = listOf()
        chineseMapping["#"] = listOf()
        T9_MAPPING.add(chineseMapping)

        val uyMapping: MutableMap<String, List<String>> = mutableMapOf()
        uyMapping["1"] = listOf()
        uyMapping["2"] = listOf("ئا", "ئە", "ب", "س")
        uyMapping["3"] = listOf("د", "ئې", "ف")
        uyMapping["4"] = listOf("گ", "غ", "خ", "ھ", "ئى")
        uyMapping["5"] = listOf("ج", "ك", "ق", "ل")
        uyMapping["6"] = listOf("م", "ن", "ئو", "ڭ")
        uyMapping["7"] = listOf("پ", "چ", "ر", "ژ")
        uyMapping["8"] = listOf("ت", "ئۇ", "ئۆ", "ئۈ")
        uyMapping["9"] = listOf("ۋ", "ش", "ي", "ز")
        uyMapping["*"] = listOf()
        uyMapping["0"] = listOf()
        uyMapping["#"] = listOf()
        T9_MAPPING.add(uyMapping)


        T9_MAPPING.forEach { itemMapping ->
            itemMapping.iterator().forEach { item->
                item.value.forEach { key ->
                    keyToNumMapping[key] = item.key
                }
            }
        }
        keyToNumMapping["ا"] = "2"
        keyToNumMapping["ە"] = "2"
        keyToNumMapping["ې"] = "3"
        keyToNumMapping["ى"] = "4"
        keyToNumMapping["و"] = "6"
        keyToNumMapping["ۇ"] = "8"
        keyToNumMapping["ۆ"] = "8"
        keyToNumMapping["ۈ"] = "8"

        keyToNumMapping["2"] = "2"
        keyToNumMapping["3"] = "3"
        keyToNumMapping["4"] = "4"
        keyToNumMapping["5"] = "5"
        keyToNumMapping["6"] = "6"
        keyToNumMapping["7"] = "7"
        keyToNumMapping["8"] = "8"
        keyToNumMapping["9"] = "9"
    }

}