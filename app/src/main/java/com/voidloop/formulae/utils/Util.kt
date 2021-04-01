package com.voidloop.formulae.utils

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import retrofit2.Response

inline fun <reified T> Response<*>.parseErrorResponse(): T? {
    val moshi = Moshi.Builder().build()
    val parser = moshi.adapter(T::class.java)
    val response = errorBody()?.string()
    if (response != null)
        try {
            return parser.fromJson(response)
        } catch (e: JsonDataException) {
            e.printStackTrace()
        }
    return null
}