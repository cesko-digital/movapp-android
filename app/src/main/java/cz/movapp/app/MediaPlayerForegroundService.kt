package cz.movapp.app

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class MediaPlayerForegroundService : Service()  {
    private val channelId = "MediaPlayerForegroundServiceChannel"

    private var player: MediaPlayer? = null
    private var fileName: String? = null
    private var handler: Handler? = null
    private var runnableCheck : DelayedChecker? = null
    private var playerPaused = false;
    private var wakeLock : PowerManager.WakeLock? = null

    private var broadcastMediaStateReceiver : BroadcastReceiver? = null
    private var broadcastMediaSeekToReceiver : BroadcastReceiver? = null

    class DelayedChecker(
        private val handler: Handler,
        private val delayTime: Long,
        private val cond: ()-> Boolean,
        private val checker: () -> Unit): Runnable {

        override fun run() {
            checker()
            if (cond()) {
                handler.postDelayed(this, delayTime)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        fileName = intent?.getStringExtra("fileName")
        val toName = intent?.getStringExtra("toName")
        val fromName = intent?.getStringExtra("fromName")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(
                1,
                notificationToDisplayServiceInform(
                    if (toName != null) toName!! else "",
                    if (fromName != null) fromName!! else "",
                )
            )
        }

        lockCpu()

        handler = Looper.myLooper()?.let { Handler(it) }

        player = MediaPlayer().apply {
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        }

        player!!.setScreenOnWhilePlaying(true)

        val afd: AssetFileDescriptor = applicationContext.assets.openFd(fileName!!)
        player!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length);
        afd.close()

        player!!.setAudioAttributes(
            AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        )

        runnableCheck = DelayedChecker(
            handler!!,
            200,
            { player!!.isPlaying },
            {
                sendCurrentTimeToActivity(player!!.currentPosition, player!!.duration)
            }
        )

        player!!.setOnPreparedListener {
            playerPaused = true
            player!!.seekTo(0)
            handler!!.postDelayed(runnableCheck!!, 200)
        }

        player!!.setOnCompletionListener {
            player!!.stop()
            player!!.prepareAsync()
            sendMediaPlayerStateToActivity("stop")
        }

        player!!.prepareAsync()

        broadcastMediaStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intend: Intent?) {
                when (intend?.getStringExtra("state")) {
                    "start" -> {
                        if (playerPaused) {
                            player!!.start()

                            sendCurrentTimeToActivity(player!!.currentPosition, player!!.duration)

                            handler!!.postDelayed(runnableCheck!!, 200)
                        }

                        playerPaused = false
                    }

                    "pause" -> {
                        if (player!!.isPlaying) {
                            player!!.pause()
                            playerPaused = true
                        }
                    }

                    "stopService" -> {
                        stopForeground(true)
                        stopSelf()
                    }
                }
            }
        }

        broadcastMediaSeekToReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intend: Intent?) {
                val seekTo = intend?.getIntExtra("seekTo", 0)

                if (player!!.isPlaying || playerPaused) {
                    player!!.seekTo(seekTo!!)
                }
            }
        }

        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            broadcastMediaStateReceiver!!,
            IntentFilter("MediaPlayerState")
        )

        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            broadcastMediaSeekToReceiver!!,
            IntentFilter("MediaPlayerSeekTo")
        )

        return START_REDELIVER_INTENT
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun notificationToDisplayServiceInform(toName: String, fromName: String): Notification {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_MUTABLE
        )
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(toName)
            .setContentText(fromName)
            .setSmallIcon(R.drawable.player_play)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun sendMediaPlayerStateToActivity(state: String) {
        val intent = Intent("MediaPlayerState")
        intent.putExtra("state", state)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun sendCurrentTimeToActivity(current : Int, duration : Int) {
        val intent = Intent("MediaPlayerTime")
        intent.putExtra("current", current)
        intent.putExtra("duration", duration)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun lockCpu() {
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Movapp::WakeLockFairyTale").apply {
                    acquire()
                }
            }
    }

    private fun unLockCpu() {
        wakeLock!!.release()
        wakeLock = null
    }

    override fun onDestroy() {
        super.onDestroy()

        unLockCpu()

        if (broadcastMediaStateReceiver != null) {
            LocalBroadcastManager.getInstance(applicationContext)
                .unregisterReceiver(broadcastMediaStateReceiver!!)
            broadcastMediaStateReceiver = null
        }
        if (broadcastMediaSeekToReceiver != null) {
            LocalBroadcastManager.getInstance(applicationContext)
                .unregisterReceiver(broadcastMediaSeekToReceiver!!)
            broadcastMediaSeekToReceiver = null
        }

        if (handler != null && runnableCheck != null) {
            handler!!.removeCallbacks(runnableCheck!!)
        }

        player!!.reset()
        player = null
    }
}