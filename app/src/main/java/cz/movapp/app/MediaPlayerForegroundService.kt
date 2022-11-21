package cz.movapp.app

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.AssetFileDescriptor
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.IOException


class MediaPlayerForegroundService : Service()  {
    private val channelId = "MediaPlayerForegroundServiceChannel"
    private val notificationId = 1233456

    private var player: MediaPlayer? = null
    private var mediaSession : MediaSession? = null
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
        val slug = intent?.getStringExtra("slug")
        val newFileName = intent?.getStringExtra("fileName")
        val toName = intent?.getStringExtra("toName")
        val fromName = intent?.getStringExtra("fromName")

        if (player != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = getSystemService(
                    NotificationManager::class.java
                )

                manager.notify(notificationId, createNotification(slug!!, toName!!, fromName!!))
            }

            if (fileName != newFileName) {
                fileName = newFileName
                initMediaPlayer(slug!!, toName!!, fromName!!, true)
            }

            if (player!!.isPlaying) {
                sendMediaPlayerState("start")
            } else {
                sendMediaPlayerState("pause")
            }

            return START_STICKY
        }

        fileName = newFileName

        lockCpu()

        mediaSession = MediaSession(this, "Movapp Fairy Tale Media Session")

        handler = Looper.myLooper()?.let { Handler(it) }

        player = MediaPlayer().apply {
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        }

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
                sendCurrentTime(player!!.currentPosition, player!!.duration)
                updateNotificationState()
            }
        )

        initMediaPlayer(slug!!, toName!!, fromName!!,false)

        broadcastMediaStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intend: Intent?) {
                when (intend?.getStringExtra("state")) {
                    "start" -> {
                        if (playerPaused) {
                            player!!.start()

                            sendCurrentTime(player!!.currentPosition, player!!.duration)

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(
                notificationId,
                notificationToDisplayServiceInform(slug!!, toName!!, fromName!!)
            )
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                "Movapp fairy tale notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            serviceChannel.setSound(null,null)
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun updateNotificationSessionMetadata(slug: String, toName: String, fromName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val bitmap = try {
                val imageStream = applicationContext.assets.open("stories/${slug}/thumbnail.webp")
                BitmapFactory.decodeStream(imageStream)
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                null
            }

            val metadataBuilder = MediaMetadata.Builder().apply {
                putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, toName)
                putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, fromName)
                putString(MediaMetadata.METADATA_KEY_TITLE, toName)
                putString(MediaMetadata.METADATA_KEY_ARTIST, fromName)
                putBitmap(MediaMetadata.METADATA_KEY_ART, bitmap)
                putLong(MediaMetadata.METADATA_KEY_DURATION, player!!.duration.toLong())
            }

            mediaSession!!.setMetadata(metadataBuilder.build())
        }
    }

    private fun createNotification(slug: String, toName: String, fromName: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_MUTABLE
        )

        val bitmap = try {
            val imageStream = applicationContext.assets.open("stories/${slug}/thumbnail.webp")
            BitmapFactory.decodeStream(imageStream)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            null
        }

        return Notification.Builder(this, channelId)
            .setContentTitle(toName)
            .setContentText(fromName)
            .setSmallIcon(R.drawable.player_play)
            .setContentIntent(pendingIntent)
            .setLargeIcon(bitmap)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setStyle(Notification.MediaStyle().setMediaSession(mediaSession!!.sessionToken))
            .build()
    }

    private fun notificationToDisplayServiceInform(slug: String, toName: String, fromName: String): Notification {
        createNotificationChannel()
        return createNotification(slug, toName, fromName)
    }

    private fun updateNotificationState() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!player!!.isPlaying) {
                return
            }

            val stateBuilder = PlaybackState.Builder()
                .setState(
                    PlaybackState.STATE_PLAYING,
                    player!!.currentPosition.toLong(),
                    1.0f
                )
            mediaSession!!.setPlaybackState(stateBuilder.build())
        }
    }

    private fun sendMediaPlayerState(state: String) {
        val intent = Intent("MediaPlayerState")
        intent.putExtra("state", state)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun sendCurrentTime(current : Int, duration : Int) {
        val intent = Intent("MediaPlayerTime")
        intent.putExtra("isPlaying", player!!.isPlaying)
        intent.putExtra("current", current)
        intent.putExtra("duration", duration)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun initMediaPlayer(slug: String, toName: String, fromName: String, reset: Boolean) {
        if (reset) {
            player!!.reset()
        }

        player!!.setOnPreparedListener {
            playerPaused = true
            player!!.seekTo(0)
            handler!!.postDelayed(runnableCheck!!, 200)
            mediaSession!!.isActive = true

            updateNotificationSessionMetadata(slug, toName, fromName)
        }

        player!!.setOnCompletionListener {
            sendMediaPlayerState("stop")
            playerPaused = true
        }

        try {
            val afd: AssetFileDescriptor = applicationContext.assets.openFd(fileName!!)
            player!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length);
            afd.close()
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        player!!.prepareAsync()
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
            handler = null
        }

        player!!.reset()
        player = null
    }
}