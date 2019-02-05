package at.shockbytes.corey.data.user

import android.content.Context
import at.shockbytes.core.model.ShockbytesUser
import at.shockbytes.corey.R
import com.google.firebase.auth.FirebaseAuth

/**
 * Author:  Martin Macheiner
 * Date:    04.03.2018
 */
class FirebaseUserRepository(
    private val context: Context
) : UserRepository {

    override val user: ShockbytesUser
        get() {
            val fbUser = FirebaseAuth.getInstance().currentUser!!
            return ShockbytesUser(
                    fbUser.displayName ?: context.getString(R.string.anonymous_user),
                    fbUser.displayName ?: context.getString(R.string.anonymous_user),
                    fbUser.email,
                    fbUser.photoUrl,
                    fbUser.providerId,
                    null)
        }

    override fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}