package at.shockbytes.corey.util

import io.reactivex.Completable

fun completableOf(action: () -> Unit): Completable {
    return Completable.fromAction(action)
}