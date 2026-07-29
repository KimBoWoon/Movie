package com.cheeke.surfy.network.impl

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import kotlin.test.DefaultAsserter.fail

@RunWith(value = RobolectricTestRunner::class)
class NetworkErrorTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var movieApis: MovieApis

    private val json = Json

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addCallAdapterFactory(CustomCallAdapter())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        movieApis = retrofit.create(MovieApis::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun unknownHostErrorTest() = runTest {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://unknown.host.that.doesnt.exist/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(CustomCallAdapter())
            .build()
        val api = retrofit.create(MovieApis::class.java)

        when (val result = api.getMovie(id = 0)) {
            is ApiResponse.Failure -> {
                assert(result.stringRes == R.string.un_known_host)
            }
            is ApiResponse.Success -> fail("Failure여야 해요")
        }
    }

    @Test
    fun disconnectNetworkTest() = runTest {
        mockWebServer.enqueue(
            response = MockResponse()
                .setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)
                .setBody("""{"message": "네트워크가 끊어졌습니다."}""")
        )

        when (val result = movieApis.getMovie(id = 0)) {
            is ApiResponse.Failure -> {
                assert(result.stringRes == R.string.network_error_unknown)
            }
            is ApiResponse.Success -> {}
        }
    }

    @Test
    fun timeoutErrorTest() = runTest {
        mockWebServer.enqueue(
            response = MockResponse()
                .setBodyDelay(10, TimeUnit.HOURS)
                .setBody("""{"message": "타임아웃 에러입니다"}""")
        )

        when (val result = movieApis.getMovie(id = 0)) {
            is ApiResponse.Failure -> {
                assert(result.stringRes == R.string.soket_timeout)
            }
            is ApiResponse.Success -> {}
        }
    }

    @Test
    fun serverErrorTest() = runTest {
        mockWebServer.enqueue(
            response = MockResponse()
                .setResponseCode(500)
                .setBody("""{"message": "서버 에러입니다"}""")
        )

        when (val result = movieApis.getMovie(id = 0)) {
            is ApiResponse.Failure -> {
                assert(result.stringRes == R.string.server_error)
                assert(result.code == 500)
            }
            is ApiResponse.Success -> {}
        }
    }

    @Test
    fun authorizedErrorTest() = runTest {
        mockWebServer.enqueue(
            response = MockResponse()
                .setResponseCode(401)
        )

        when (val result = movieApis.getMovie(id = 0)) {
            is ApiResponse.Failure -> {
                assert(result.stringRes == R.string.user_error)
                assert(result.code == 401)
            }
            is ApiResponse.Success -> {}
        }
    }
}