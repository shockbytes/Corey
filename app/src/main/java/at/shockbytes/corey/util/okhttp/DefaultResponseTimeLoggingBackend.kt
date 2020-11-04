package at.shockbytes.corey.util.okhttp

import timber.log.Timber

class DefaultResponseTimeLoggingBackend : ResponseTimeLoggingBackend {

    override fun logResponseTime(dataPacket: ResponseTimeDataPacket.Log) {
        with(dataPacket) {
            Timber.d("Response time for $url with query parameters $queryParameters: ${tookMs}ms\nFurther details:\nApp version: $appVersionName / $appVersionCode\nDevice: $model / $sdkVersion")
        }
    }

    override fun logResponseError(dataPacket: ResponseTimeDataPacket.Error) {
        with(dataPacket) {
            Timber.e(exception, "Exception for $url: ${exception.message}")
        }
    }
}