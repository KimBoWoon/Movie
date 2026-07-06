package com.cheeke.surfy.network

import com.cheeke.surfy.network.model.SurfyNetworkErrorMessage
import com.cheeke.surfy.network.model.SurfyResponseErrorMessage
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

internal class RxApiResultCallAdapter<R>(
    private val successType: Type,
    private val reactiveType: Class<*>
) : CallAdapter<R, Any> {
    override fun responseType(): Type = successType

    override fun adapt(call: Call<R>): Any {
        val single = Single.create<ApiResponse<R>> { emitter ->
            call.enqueue(object : Callback<R> {
                override fun onResponse(
                    call: Call<R>,
                    response: Response<R>
                ) {
                    emitter.onSuccess(response.toApiResponse())
                }

                override fun onFailure(
                    call: Call<R>,
                    t: Throwable
                ) {
                    emitter.onSuccess(t.toFailure())
                }
            })
        }

        return when (reactiveType) {
            Single::class.java -> single
            Maybe::class.java -> single.toMaybe()
            Observable::class.java -> single.toObservable()
            Flowable::class.java -> single.toFlowable()
            else -> error("Unsupported reactive type.")
        }
    }
}

private fun <R> Response<R>.toApiResponse(): ApiResponse<R> {
    return when (code()) {
        in 200..299 -> {
            body()?.let {
                ApiResponse.Success(it)
            } ?: ApiResponse.Failure(
                throwable = IllegalStateException("Empty Body")
            )
        }
        in 400..499 -> {
            ApiResponse.Failure(
                code = code(),
                stringRes = SurfyResponseErrorMessage.USER_ERROR.stringRes,
                body = errorBody()?.string()
            )
        }
        in 500..599 -> {
            ApiResponse.Failure(
                code = code(),
                stringRes = SurfyResponseErrorMessage.SERVER_ERROR.stringRes,
                body = errorBody()?.string()
            )
        }
        else -> {
            ApiResponse.Failure(
                code = code(),
                stringRes = SurfyResponseErrorMessage.UNKNOWN.stringRes,
                body = errorBody()?.string()
            )
        }
    }
}

private fun Throwable.toFailure(): ApiResponse.Failure {
    val stringRes = when (this) {
        is UnknownHostException -> SurfyNetworkErrorMessage.UN_KNOWN_HOST.stringRes
        is SocketTimeoutException -> SurfyNetworkErrorMessage.SOCKET_TIMEOUT.stringRes
        is ConnectException -> SurfyNetworkErrorMessage.CONNECT_EXCEPTION.stringRes
        is SSLHandshakeException -> SurfyNetworkErrorMessage.SSL_HAND_SHAKE_EXCEPTION.stringRes
        else -> SurfyNetworkErrorMessage.UNKNOWN.stringRes
    }

    return ApiResponse.Failure(
        throwable = this,
        stringRes = stringRes
    )
}

//internal class ApiResultCallAdapter<R>(
//    private val successType: Type
//) : CallAdapter<R, Call<ApiResponse<R>>> {
//    override fun adapt(call: Call<R>): Call<ApiResponse<R>> = ApiResultCall(call, successType)
//
//    override fun responseType(): Type = successType
//}