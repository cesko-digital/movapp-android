package digital.cesko.movapp.data

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import digital.cesko.movapp.adapter.playSound

import org.junit.Test
import org.junit.runner.RunWith
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class AlphabetDatasourceTest {

    @Test
    fun loadByLanguage_openAssets_throwsNoException() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val dataSource = AlphabetDatasource(appContext)

        assertThat(openAlphabetLetterSoundAssetFiles(dataSource, appContext, "cs")).isNotEmpty()
        assertThat(openAlphabetLetterSoundAssetFiles(dataSource, appContext, "uk")).isNotEmpty()
    }

    private fun openAlphabetLetterSoundAssetFiles(
        dataSource: AlphabetDatasource,
        appContext: Context,
        langCode: String
    ): List<InputStream?> {
        val alphabet = dataSource.loadByLanguage(langCode)
        return alphabet
            .map { alphabetData -> alphabetData.letterSoundAssetFile?.let { appContext.assets.open(it) } }
    }

    @Test
    fun playSound() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        assertThat(playSound(appContext,"alphabet/cs-alphabet/e.mp3")).isNotNull()
    }
}