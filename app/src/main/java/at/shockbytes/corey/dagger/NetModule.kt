package at.shockbytes.corey.dagger

import at.shockbytes.corey.util.okhttp.FirebaseResponseTimeLoggingBackend
import at.shockbytes.corey.util.okhttp.ResponseTimeLoggingBackend
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptors: Array<Interceptor>): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .apply {
                interceptors.forEach(::addInterceptor)
            }
            .build()
    }

    @Provides
    fun provideOkHttpInterceptors(
        loggingBackend: ResponseTimeLoggingBackend
    ): Array<Interceptor> {
        return arrayOf(
            // ResponseTimeInterceptor(loggingBackend)
            /*
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            },
             */
        )
    }

    @Provides
    fun provideResponseTimeLoggingBackend(
        firebase: FirebaseDatabase
    ): ResponseTimeLoggingBackend {
        val ref = firebase.getReference(FirebaseModule.REF_RESPONSE_TIME_METRICS)
        return FirebaseResponseTimeLoggingBackend(ref)
    }
}