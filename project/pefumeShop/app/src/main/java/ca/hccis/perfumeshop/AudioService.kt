package ca.hccis.perfumeshop

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.Settings

class AudioService : Service() {
    private lateinit var mediaPlayer: MediaPlayer

    // This runs the moment the service is called
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Grab the phone's default notification chime
        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_NOTIFICATION_URI)

        // When the sound finishes playing, completely shut down the background service
        mediaPlayer.setOnCompletionListener {
            stopSelf()
        }

        mediaPlayer.start()

        return START_NOT_STICKY
    }

    // We don't need to bind this service to the UI, so we return null
    override fun onBind(intent: Intent?): IBinder? = null

    // Cleanup memory when it shuts down
    override fun onDestroy() {
        super.onDestroy()
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}