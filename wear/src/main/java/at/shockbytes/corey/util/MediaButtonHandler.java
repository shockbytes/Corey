package at.shockbytes.corey.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemClock;
import android.view.KeyEvent;

/**
 * @author Martin Macheiner
 *         Date: 04.03.2017.
 */

public class MediaButtonHandler {

    private Context context;

    private AudioManager audioManager;

    public MediaButtonHandler(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void stop() {
        sendIntents(KeyEvent.KEYCODE_MEDIA_PAUSE);
    }

    public void playPause() {
        sendIntents(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
    }

    public void play() {
        sendIntents(KeyEvent.KEYCODE_MEDIA_PLAY);
    }

    public void next() {
        sendIntents(KeyEvent.KEYCODE_MEDIA_NEXT);
    }

    public void previous() {
        sendIntents(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
    }

    public boolean isMusicPlayed() {
        return audioManager.isMusicActive();
    }

    private void sendIntents(int keycode) {

        long eventTime = SystemClock.uptimeMillis();

        Intent down = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT,
                new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keycode, 0));
        context.sendOrderedBroadcast(down, null);

        Intent up = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT,
                new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keycode, 0));
        context.sendOrderedBroadcast(up, null);
    }

}
