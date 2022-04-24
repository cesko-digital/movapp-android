package cz.movapp.app.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cz.movapp.app.BuildConfig
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

        binding.textAboutVersion.text = resources.getString(R.string.about_version).format(BuildConfig.VERSION_NAME)
        binding.textAboutBuild.text = resources.getString(R.string.about_build).format(BuildConfig.VERSION_CODE)

        binding.textAboutWeb.setOnClickListener {
            openUri(HTTP_MOVAPP_WEB)
        }

        binding.textAboutTwitter.setOnClickListener {
            openUri(HTTP_MOVAPP_TWITTER)
        }

        binding.textAboutInstagram.setOnClickListener {
            openUri(HTTP_MOVAPP_INSTAGRAM)
        }

        binding.textAboutSuggestion.setOnClickListener {
            openUri(HTTP_MOVAPP_SUGGESTION)
        }

        binding.textAboutLicense.setOnClickListener {
            openUri(HTTP_MOVAPP_LICENCE)
        }


        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                _binding = null
            }
        })

        return binding.root
    }

    private fun openUri(textUri: String) {
        val queryUrl: Uri = Uri.parse(textUri)
        val intent = Intent(Intent.ACTION_VIEW, queryUrl)
        context?.startActivity(intent)
    }

}