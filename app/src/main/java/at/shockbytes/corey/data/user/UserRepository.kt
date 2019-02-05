package at.shockbytes.corey.data.user

import at.shockbytes.core.model.ShockbytesUser

/**
 * Author:  Martin Macheiner
 * Date:    04.03.2018
 */
interface UserRepository {

    val user: ShockbytesUser

    fun signOut()
}