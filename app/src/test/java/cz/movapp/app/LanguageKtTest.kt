package cz.movapp.app

import com.google.common.truth.Truth.assertThat
import cz.movapp.app.data.LanguagePair
import org.junit.Test
import java.util.Locale


class LanguageKtTest{

    @Test
    fun getInitialLang() {
        Locale.setDefault(Locale.forLanguageTag("cs"))
        assertThat(LanguagePair.getDefault()).isEqualTo(LanguagePair.CsToUk)
    }
}