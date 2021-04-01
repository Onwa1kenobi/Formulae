package com.voidloop.formulae.data

import android.content.Context
import com.squareup.moshi.Moshi
import com.voidloop.formulae.data.local.AppLocalDatabase
import com.voidloop.formulae.data.local.FormulaEntity
import com.voidloop.formulae.data.model.BaseResponseModel
import com.voidloop.formulae.utils.parseErrorResponse
import com.voidloop.formulae.utils.NetworkEventManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class DataSource(context: Context) {
    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

    private val moshi = Moshi.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    private val database = AppLocalDatabase.getInstance(context)

    suspend fun normalizeTexFormula(input: String): Result<String> =
        withContext(Dispatchers.IO) {
            val localResult = findFormulaLocally(input)
            if (localResult is Result.Success) {
                return@withContext Result.Success(localResult.data.formula)
            }

            val isNetworkConnected = NetworkEventManager.value?.networkState?.isConnected ?: false
            if (isNetworkConnected.not()) {
                return@withContext Result.Failure(
                    LocalException("Network Error"),
                    "Internet connection is not available"
                )
            }

            val map = mutableMapOf<String, RequestBody>()
            val formulaBody = input.toRequestBody(MultipartBody.FORM)
            map["q"] = formulaBody

            val response = apiService.normalizeTeXFormula(map)
            if (response.isSuccessful && response.body() != null) {
                val xResourceLocation = response.headers()["x-resource-location"]
                renderFormula(input, xResourceLocation.orEmpty())
            } else {
                val error = response.parseErrorResponse<BaseResponseModel>()
                Result.Failure(LocalException("Failed to fetch from api"), error?.title)
            }
        }

    private suspend fun renderFormula(input: String, resourceLocation: String): Result<String> =
        withContext(Dispatchers.IO) {
            val response = apiService.renderFormula(resourceLocation)
            if (response.isSuccessful && response.body().isNullOrBlank().not()) {
                val formula = response.body()!!

                val localResult = findFormulaLocally(input)
                if (localResult is Result.Failure) {
                    val formulaEntity = FormulaEntity(input = input, formula = formula)
                    database.FormulaDao().insertFormula(formulaEntity)
                }

                Result.Success(formula)
            } else {
                val error = response.parseErrorResponse<BaseResponseModel>()
                Result.Failure(LocalException("Failed to fetch from api"), error?.title)
            }
        }

    suspend fun fetchLocalList(input: String): Result<List<FormulaEntity>> =
        withContext(Dispatchers.IO) {
            val formulas = database.FormulaDao().suggestFormulas(input)
            Result.Success(formulas)
        }

    private suspend fun findFormulaLocally(input: String): Result<FormulaEntity> =
        withContext(Dispatchers.IO) {
            val formula = database.FormulaDao().searchFormula(input)
            if (formula != null) {
                Result.Success(formula)
            } else {
                Result.Failure(
                    LocalException("Failed to fetch from api"),
                    "Formula not found locally"
                )
            }
        }
}