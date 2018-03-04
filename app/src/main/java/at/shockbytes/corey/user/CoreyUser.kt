package at.shockbytes.corey.user

import android.net.Uri

/**
 * @author Martin Macheiner
 * Date: 04-Mar-18.
 */

data class CoreyUser(val name: String, val photoUrl: Uri?,
                     val email: String?, val providerId: String)