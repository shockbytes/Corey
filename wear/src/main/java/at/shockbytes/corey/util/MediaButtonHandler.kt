package at.shockbytes.corey.util

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.SystemClock
import android.view.KeyEvent

/**
 * @author Martin Macheiner
 * Date: 04.03.2017.
 */

class MediaButtonHandler(private val context: Context) {

    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    val isMusicPlayed: Boolean
        get() = audioManager.isMusicActive

    fun stop() {
        sendIntents(KeyEvent.KEYCODE_MEDIA_PAUSE)
    }

    fun playPause() {
        sendIntents(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
    }

    fun play() {
        sendIntents(KeyEvent.KEYCODE_MEDIA_PLAY)
    }

    operator fun next() {
        sendIntents(KeyEvent.KEYCODE_MEDIA_NEXT)
    }

    fun previous() {
        sendIntents(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
    }

    private fun sendIntents(keycode: Int) {

        val eventTime = SystemClock.uptimeMillis()

        val down = Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT,
                KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keycode, 0))
        context.sendOrderedBroadcast(down, null)

        val up = Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT,
                KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keycode, 0))
        context.sendOrderedBroadcast(up, null)
    }
}
