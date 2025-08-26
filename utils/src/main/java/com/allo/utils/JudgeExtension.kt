package com.allo.utils

import androidx.annotation.IntDef
import com.allo.utils.Judge.Companion.FALSE
import com.allo.utils.Judge.Companion.TRUE

@Retention(AnnotationRetention.SOURCE)
@IntDef(TRUE,FALSE)
annotation class Judge{
    companion object{
        const val TRUE = 1
        const val FALSE = 0
    }
}

fun Int?.isTrue(trueValue:Int = TRUE):Boolean{
    return this == trueValue
}

