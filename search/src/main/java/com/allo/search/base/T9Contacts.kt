package com.allo.search.base

import androidx.annotation.IntDef
import com.allo.search.base.T9Contacts.SearchType.Companion.ALL_SPELLING
import com.allo.search.base.T9Contacts.SearchType.Companion.PHONE
import com.allo.search.base.T9Contacts.SearchType.Companion.PINYIN

interface T9Contacts : Contact {

    @SearchType
    fun searchType(): Int



    @IntDef(value = [PINYIN, PHONE, ALL_SPELLING])
    @Retention(AnnotationRetention.SOURCE)
    annotation class SearchType {
        companion object {
            const val PINYIN = 2
            const val PHONE = 3
            const val ALL_SPELLING = 4
        }
    }
}