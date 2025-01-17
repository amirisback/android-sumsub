package com.sumsub.idensic.manager

import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.google.gson.Gson
import com.sumsub.idensic.App
import com.sumsub.idensic.BuildConfig
import com.sumsub.idensic.model.AccessTokenResponse
import com.sumsub.idensic.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiManager(private val apiUrl: String, isSandBox: () -> Boolean, clientId: () -> String?) {

    private val service: ApiService by lazy { retrofit.create(ApiService::class.java) }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(apiUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val gson: Gson by lazy { Gson() }

    private val okHttpClient: OkHttpClient by lazy {

        val chuck = ChuckerInterceptor.Builder(App.getContext())
            .collector(ChuckerCollector(App.getContext()))
            .maxContentLength(250000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()

        OkHttpClient.Builder()
            .addInterceptor(DemoHeadersInterceptor(isSandBox, clientId))
            .addInterceptor(chuck)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG)
                        HttpLoggingInterceptor.Level.BODY
                    else
                        HttpLoggingInterceptor.Level.NONE
            })
            .build()
    }

    suspend fun getAccessTokenForLevel(token: String?, userId: String, levelName: String?): AccessTokenResponse =
        service.getAccessToken(authorization = "Bearer $token", levelName = levelName, userId = userId)

    suspend fun getAccessTokenForAction(
        token: String?,
        userId: String,
        levelName: String?,
        actionId: String?
    ): AccessTokenResponse =
        service.getAccessToken(
            authorization = "Bearer $token",
            levelName = levelName,
            userId = userId,
            externalActionId = actionId
        )

    suspend fun getFlows(authorizationToken: String) = service.getFlows(authorization = "Bearer $authorizationToken")

    suspend fun getLevels(authorizationToken: String) = service.getLevels(authorization = "Bearer $authorizationToken")

}