package com.cheeke.surfy.network

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class RxApiResultCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)
        val reactiveType = when (rawType) {
            Single::class.java,
            Maybe::class.java,
            Observable::class.java,
            Flowable::class.java -> rawType
            else -> return null
        }

        check(returnType is ParameterizedType)

        val apiResponseType = getParameterUpperBound(0, returnType)

        require(getRawType(apiResponseType) == ApiResponse::class.java) {
            "Return type must be ApiResponse<T>"
        }

        check(apiResponseType is ParameterizedType)

        val successType = getParameterUpperBound(0, apiResponseType)

        return RxApiResultCallAdapter<Any>(
            successType = successType,
            reactiveType = reactiveType
        )
    }
}

//class CustomCallAdapter @Inject constructor() : CallAdapter.Factory() {
//    override fun get(
//        returnType: Type,
//        annotations: Array<out Annotation>,
//        retrofit: Retrofit
//    ): CallAdapter<*, *>? {
//        if (getRawType(returnType) != Call::class.java) return null
//        check(returnType is ParameterizedType) {
//            "return type defined as ApiResponse<out T>"
//        }
//
//        val wrapperType = getParameterUpperBound(0, returnType)
//        if (getRawType(wrapperType) != ApiResponse::class.java) return null
//        check(wrapperType is ParameterizedType) {
//            "return type defined as ApiResponse<ResponseBody>"
//        }
//
//        val bodyType = getParameterUpperBound(0, wrapperType)
//        return ApiResultCallAdapter<Any>(bodyType)
//    }
//}