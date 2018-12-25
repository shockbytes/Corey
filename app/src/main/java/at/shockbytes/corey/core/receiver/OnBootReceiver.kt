package at.shockbytes.corey.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import at.shockbytes.corey.core.CoreyApp
import at.shockbytes.corey.data.schedule.ScheduleRepository
import javax.inject.Inject

class OnBootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var manager: ScheduleRepository

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as CoreyApp).appComponent.inject(this)
        manager.poke()
    }
}
