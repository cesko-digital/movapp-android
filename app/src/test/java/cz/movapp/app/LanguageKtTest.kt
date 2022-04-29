package cz.movapp.app

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.*


class LanguageKtTest{

    @Test
    fun getInitialLang() {
        Locale.setDefault(Locale.forLanguageTag("cs"))
        assertThat(LanguagePair.getDefault()).isEqualTo(LanguagePair.CsToUk)
    }
}