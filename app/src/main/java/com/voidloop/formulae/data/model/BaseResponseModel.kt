package com.voidloop.formulae.data.model

import com.squareup.moshi.Json

open class BaseResponseModel {
    @field:Json(name = "type")
    val type: String = ""

    @field:Json(name = "title")
    val title: String = ""

    @field:Json(name = "details")
    val details: String = ""

    @field:Json(name = "instance")
    val instance: String = ""
}