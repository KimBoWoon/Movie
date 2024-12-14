package com.bowoon.network

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class ApiResultCallAdapter<R>(
    private val successType: Type
) : CallAdapter<R, Call<ApiResponse<R>>> {
    override fun adapt(call: Call<R>): Call<ApiResponse<R>> = ApiResultCall(call, successType)

    override fun responseType(): Type = successType
}