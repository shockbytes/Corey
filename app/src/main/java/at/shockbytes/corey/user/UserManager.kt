package at.shockbytes.corey.user

import android.graphics.Bitmap
import android.support.v4.app.FragmentActivity
import io.reactivex.Single

/**
 * @author Martin Macheiner
 * Date: 04-Mar-18.
 */

interface UserManager {

    val user: CoreyUser

    fun signOut()

    fun loadAccountImage(activity: FragmentActivity): Single<Bitmap>

}