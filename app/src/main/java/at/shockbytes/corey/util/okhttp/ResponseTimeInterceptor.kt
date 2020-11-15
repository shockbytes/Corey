package at.shockbytes.corey.util.okhttp

import android.os.Build
import at.shockbytes.corey.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * TODO Move to ShockUtil library
 */
class ResponseTimeInterceptor(
    private val loggingBackend: ResponseTimeLoggingBackend
) : Interceptor {

    private val model = Build.MODEL
    private val sdkVersion = Build.VERSION.SDK_INT
    private val appVersion = BuildConfig.VERSION_NAME
    private val appVersionCode = BuildConfig.VERSION_CODE

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val url = request.url

        val queryParameters: Map<String, String?> = url
            .queryParameterNames
            .associateWith { name -> url.queryParameter(name) }

        val startNs = System.nanoTime()
        return try {
            chain.proceed(request).also {
                val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
                loggingBackend.logResponseTime(
                    ResponseTimeDataPacket.ofLog(
                        url.withoutQuery,
                        queryParameters,
                        model,
                        sdkVersion,
                        appVersion,
                        appVersionCode,
                        tookMs
                    )
                )
            }
        } catch (e: Exception) {
            loggingBackend.logResponseError(
                ResponseTimeDataPacket.ofError(
                    url.withoutQuery,
                    queryParameters,
                    model,
                    sdkVersion,
                    appVersion,
                    appVersionCode,
                    e
                )
            )
            throw e
        }
    }

    private val HttpUrl.withoutQuery: String
        get() = "$scheme://$host$encodedPath"
}