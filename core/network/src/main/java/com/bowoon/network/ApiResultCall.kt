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
    override fun enqueue(callback: Callback<ApiResponse<R>>) =
        delegate.enqueue(object : Callback<R> {
            override fun onResponse(call: Call<R>, response: Response<R>) {
                response.body()?.let { body ->
                    when (response.code()) {
                        in 200..299 -> {
                            callback.onResponse(
                                this@ApiResultCall,
                                Response.success(ApiResponse.Success(body))
                            )
                        }
                        in 400..499 -> {
                            callback.onResponse(
                                this@ApiResultCall,
                                Response.success(
                                    ApiResponse.Failure(
                                        code = response.code(),
                                        message = response.message(),
                                        body = response.errorBody()?.run { string() } ?: run { null }
                                    )
                                )
                            )
                        }
                    }
                }// ?: callback.onResponse(this@ApiResultCall, Response.success(ApiResponse.Failure(throwable = IllegalStateException("empty body!"))))
            }

            override fun onFailure(call: Call<R?>, throwable: Throwable) {
                callback.onResponse(
                    this@ApiResultCall,
                    Response.success(
                        ApiResponse.Failure(throwable = throwable)
                    )
                )
            }
        })

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