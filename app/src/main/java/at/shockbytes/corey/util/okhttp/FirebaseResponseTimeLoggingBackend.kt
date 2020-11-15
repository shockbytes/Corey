package at.shockbytes.corey.util.okhttp

import com.google.firebase.database.DatabaseReference

class FirebaseResponseTimeLoggingBackend(
    private val fbDbRef: DatabaseReference
) : ResponseTimeLoggingBackend {

    override fun logResponseTime(dataPacket: ResponseTimeDataPacket.Log) {
        log(REF_LOG, dataPacket)
    }

    override fun logResponseError(dataPacket: ResponseTimeDataPacket.Error) {
        log(REF_FAILED, dataPacket)
    }

    private fun log(ref: String, data: ResponseTimeDataPacket) {
        fbDbRef.child(ref).push().apply {
            setValue(data)
        }
    }

    companion object {

        private const val REF_LOG = "log"
        private const val REF_FAILED = "failed"
    }
}