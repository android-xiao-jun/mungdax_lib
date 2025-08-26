package com.allo.utils

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import java.io.Serializable

inline fun <reified T> Fragment.arguments(key: String):Lazy<T?> = lazy {
    return@lazy arguments?.getByKey(key)
}

inline fun <reified T> Fragment.arguments(key: String, default: T):Lazy<T> = lazy {
    arguments?.getByKey(key) ?: default
}

inline fun <reified T> Fragment.safeArguments(name: String):Lazy<T> = lazy<T> {
    checkNotNull(arguments?.getByKey(name)) { "No arguments value for key \"$name\"" }
}

inline fun <reified T> Bundle.getByKey(key: String):T?{
    return when(T::class){
        Parcelable::class -> getParcelable(key) as? T
        Serializable::class -> getSerializable(key) as? T
        else -> get(key) as T
    }
}