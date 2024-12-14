package com.bowoon.network

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

internal class ApiResultCall<R>(
    private val delegate: Call<R>,
    private val successType: Type
) : Call<ApiResponse<R>> {
    override fun enqueue(callback: Callback<ApiResponse<R>>) {
        return delegate.enqueue(object : Callback<R> {
            override fun onResponse(call: Call<R>, response: Response<R>) {
                when (response.isSuccessful) {
                    true -> {
                        response.body()?.let { body ->
                            callback.onResponse(
                                this@ApiResultCall,
                                Response.success(ApiResponse.Success(body))
                            )
                        } ?: run {
                            if (successType == Unit::class.java) {
                                @Suppress("UNCHECKED_CAST")
                                callback.onResponse(
                                    this@ApiResultCall,
                                    Response.success(ApiResponse.Success(Unit as R))
                                )
                            } else {
                                ApiResponse.Failure(
                                    throwable = IllegalStateException("empty body!")
                                )
                            }
                        }
                    }
                    false -> {
                        callback.onResponse(
                            this@ApiResultCall,
                            Response.success(
                                ApiResponse.Failure(
                                    response.code(),
                                    response.message(),
                                    response.errorBody()?.run { string() } ?: run { null }
                                )
                            )
                        )
                    }
                }
            }

            override fun onFailure(call: Call<R?>, throwable: Throwable) {
                callback.onResponse(
                    this@ApiResultCall,
                    Response.success(
                        ApiResponse.Failure(throwable = throwable)
                ))
            }
        })
    }

    override fun clone(): Call<ApiResponse<R>> = ApiResultCall(delegate, successType)

    override fun execute(): Response<ApiResponse<R>> =
        throw UnsupportedOperationException("ApiResultCall doesn't support execute")

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}