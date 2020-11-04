package at.shockbytes.corey.util.okhttp

sealed class ResponseTimeDataPacket {

    abstract val url: String
    abstract val queryParameters: Map<String, String?>
    abstract val model: String
    abstract val sdkVersion: Int
    abstract val appVersionName: String
    abstract val appVersionCode: Int

    data class Log(
        override val url: String,
        override val queryParameters: Map<String, String?>,
        override val model: String,
        override val sdkVersion: Int,
        override val appVersionName: String,
        override val appVersionCode: Int,
        val tookMs: Long
    ) : ResponseTimeDataPacket()

    data class Error(
        override val url: String,
        override val queryParameters: Map<String, String?>,
        override val model: String,
        override val sdkVersion: Int,
        override val appVersionName: String,
        override val appVersionCode: Int,
        val exception: Exception
    ) : ResponseTimeDataPacket()

    companion object {

        fun ofLog(
            url: String,
            queryParameters: Map<String, String?>,
            model: String,
            sdkVersion: Int,
            appVersion: String,
            appVersionCode: Int,
            tookMs: Long
        ): Log {
            return Log(url, queryParameters, model, sdkVersion, appVersion, appVersionCode, tookMs)
        }

        fun ofError(
            url: String,
            queryParameters: Map<String, String?>,
            model: String,
            sdkVersion: Int,
            appVersion: String,
            appVersionCode: Int,
            exception: Exception
        ): Error {
            return Error(url, queryParameters, model, sdkVersion, appVersion, appVersionCode, exception)
        }
    }
}