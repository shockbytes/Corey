package at.shockbytes.corey.data.settings

enum class FirebaseDatabaseAccessMode(val acronym: String) {
    SINGLE_ACCESS("single_access"),
    USER_SCOPED_ACCESS("user_scoped_access");

    companion object {

        fun ofString(str: String): FirebaseDatabaseAccessMode? {
            return values().find { it.acronym == str }
        }
    }
}
