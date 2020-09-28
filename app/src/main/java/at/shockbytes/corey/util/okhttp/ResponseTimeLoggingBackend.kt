package at.shockbytes.corey.util.okhttp

interface ResponseTimeLoggingBackend {

    fun logResponseTime(dataPacket: ResponseTimeDataPacket.Log)

    fun logResponseError(dataPacket: ResponseTimeDataPacket.Error)
}