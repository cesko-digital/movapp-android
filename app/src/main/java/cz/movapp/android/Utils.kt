package cz.movapp.android

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer

/**
 * @return null if failed
 */
fun playSound(
    context: Context,
    assetFileName: String
): MediaPlayer? {
    val afd: AssetFileDescriptor = context.assets.openFd(assetFileName)
    var player: MediaPlayer? = MediaPlayer()
    player?.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());

    player?.setOnCompletionListener { mp ->
        mp?.reset()
        mp?.release()
        player = null
    }
    player?.prepare()
    player?.start()

    return player
}