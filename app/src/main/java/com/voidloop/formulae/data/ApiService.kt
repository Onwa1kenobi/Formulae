package com.voidloop.formulae.data

import com.voidloop.formulae.data.model.MathNormalizeResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("media/math/check/tex")
    suspend fun normalizeTeXFormula(@PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>): Response<MathNormalizeResponse>

    @GET("media/math/render/svg/{hash}")
    suspend fun renderFormula(@Path("hash") hash: String): Response<String>
}