package at.shockbytes.corey.user

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.app.FragmentActivity
import at.shockbytes.corey.R
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author  Martin Macheiner
 * Date:    04.03.2018
 */

class FirebaseUserManager(private val context: Context) : UserManager {

    override val user: CoreyUser
        get() {
            val fbUser = FirebaseAuth.getInstance().currentUser!!
            return CoreyUser(fbUser.displayName ?: context.getString(R.string.anonymous_user),
                    fbUser.photoUrl, fbUser.email, fbUser.providerId)
        }

    override fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }


    override fun loadAccountImage(activity: FragmentActivity): Single<Bitmap> {
        return Single.fromCallable {
            val photoUrl = user.photoUrl
            if (photoUrl != null) {
                Picasso.with(activity).load(photoUrl).get()
            } else {
                BitmapFactory.decodeResource(activity.resources, R.drawable.ic_account)
            }
        }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
    }

}