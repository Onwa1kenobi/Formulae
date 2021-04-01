package com.voidloop.formulae.data

sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: T) : Result<T>()

    data class Failure(val exception: Throwable, val message: String? = null) : Result<Nothing>()

    fun <In : Any, Out : Any> Success<In>.map(mapperFunc: (input: In) -> Out): Success<Out> {
        return Success(mapperFunc(this.data))
    }

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Failure -> "Error[exception=$exception]"
        }
    }
}

data class LocalException(override var message: String) : Exception()

/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val Result<*>.succeeded
    get() = this is Result.Success && data != null