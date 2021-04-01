package com.voidloop.formulae.data.model

import com.squareup.moshi.Json

class MathNormalizeResponse : BaseResponseModel() {
    @field:Json(name = "success")
    val success: Boolean = false

    @field:Json(name = "checked")
    val checked: String = ""

    @field:Json(name = "requiredPackages")
    val requiredPackages: List<String> = listOf()

    @field:Json(name = "identifiers")
    val identifiers: List<String> = listOf()

    @field:Json(name = "endsWithDot")
    val endsWithDot: Boolean = false
}