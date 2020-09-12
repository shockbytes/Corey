package at.shockbytes.corey.util

import io.reactivex.Completable

fun completableOf(action: () -> Unit): Completable {
    return Completable.fromAction(action)
}

fun completableEmitterOf(
        action: () -> Unit
): Completable {
    return Completable.create { source ->

        try {
            action()
            source.onComplete()
        } catch (e: Throwable) {
            source.onError(e)
        }
    }
}