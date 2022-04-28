package cz.movapp.app.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cz.movapp.android.LoadAssetsOtherwiseOpenInBrowserWebViewClient
import cz.movapp.android.enableDarkMode
import cz.movapp.android.openUri
import cz.movapp.app.BuildConfig
import cz.movapp.app.LanguagePair
import cz.movapp.app.MainActivity
import cz.movapp.app.R
import cz.movapp.app.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        const val HTTP_MOVAPP_WEB = "https://www.movapp.cz"
        const val HTTP_MOVAPP_TWITTER = "https://twitter.com/movappcz"
        const val HTTP_MOVAPP_INSTAGRAM = "https://instagram.com/movappcz"
        const val HTTP_MOVAPP_SUGGESTION = "https://github.com/cesko-digital/movapp-android"
        const val HTTP_MOVAPP_LICENCE = "https://github.com/cesko-digital/movapp-android/blob/main/LICENSE"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                _binding = null
            }
        })

        val mainActivity = requireActivity() as MainActivity
        setupToolbar(mainActivity)

        val context = this.requireContext()

        binding.textAboutVersion.text = resources.getString(R.string.about_version).format(BuildConfig.VERSION_NAME)
        binding.textAboutBuild.text = resources.getString(R.string.about_build).format(BuildConfig.VERSION_CODE)

        binding.textAboutWeb.setOnClickListener {
            openUri(context, HTTP_MOVAPP_WEB)
        }

        binding.textAboutTwitter.setOnClickListener {
            openUri(context, HTTP_MOVAPP_TWITTER)
        }

        binding.textAboutInstagram.setOnClickListener {
            openUri(context, HTTP_MOVAPP_INSTAGRAM)
        }

        binding.textAboutSuggestion.setOnClickListener {
            openUri(context, HTTP_MOVAPP_SUGGESTION)
        }

        binding.textAboutLicense.setOnClickListener {
            openUri(context, HTTP_MOVAPP_LICENCE)
        }



        val langCode = LanguagePair.getDefault().from.langCode
        // To get html file I used Firefox plugin Save File and them remove redundant stuff manually - ie.  kept styles and <main>
        binding.webView.loadUrl("https://appassets.androidplatform.net/assets/about/$langCode-about.html")
        binding.webView.webViewClient = LoadAssetsOtherwiseOpenInBrowserWebViewClient(context)

        //bug in WebView sometimes does not load with msg net::ERR_CACHE_MISS it still does even with this.
        binding.webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        binding.webView.enableDarkMode()

        return binding.root
    }

    private fun setupToolbar(mainActivity: MainActivity) {
        mainActivity.binding.apply {
            topAppBar.setTitle(R.string.title_about)
            topAppBar.menu.clear()
            topAppBar.invalidateMenu()
        }

        mainActivity.searchBinding.root.visibility = View.GONE

    }

}