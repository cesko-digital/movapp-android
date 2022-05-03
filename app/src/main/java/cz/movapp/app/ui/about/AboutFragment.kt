package cz.movapp.app.ui.about

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import cz.movapp.android.hideKeyboard
import cz.movapp.android.openUri
import cz.movapp.app.App
import cz.movapp.app.BuildConfig
import cz.movapp.app.R
import cz.movapp.app.data.Language
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.data.SharedPrefsRepository
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
        const val HTTP_MOVAPP_LICENCE =
            "https://github.com/cesko-digital/movapp-android/blob/main/LICENSE"
        const val HTTP_MOVAPP_LINKEDIN = "https://www.linkedin.com/company/movapp-cz"
        const val HTTP_MOVAPP_FACEBOOK = "https://www.facebook.com/movappcz"
        const val HTTP_MOVAPP_GITHUB_ANDROID = "https://github.com/cesko-digital/movapp-android"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                binding.scrollView.removeAllViews()
                _binding = null
            }
        })

        val context = this.requireContext()
        val pref = SharedPrefsRepository(context);
        val languageList = listOf(Language.Czech.stringText, Language.Ukrainian.stringText)

        ArrayAdapter(
            context,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            languageList
        ).also { adapter ->
            binding.learnChoice.adapter = adapter
            val lang = pref.getPreferedLanguage()
            if (lang == "cs") {
                val spinnerPosition = adapter.getPosition(Language.Czech.stringText)
                binding.learnChoice.setSelection(spinnerPosition)
            } else {
                val spinnerPosition = adapter.getPosition(Language.Ukrainian.stringText)
                binding.learnChoice.setSelection(spinnerPosition)
            }
        }

        binding.learnChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (adapterView?.getItemAtPosition(position).toString() == "Česky")
                    pref.setPreferedLanguage("cs")
                else
                    pref.setPreferedLanguage("uk")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.textAboutVersion.text = String.format(
            resources.getString(R.string.about_version),
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )

        binding.textAboutWeb.setOnClickListener {
            openUri(context, HTTP_MOVAPP_WEB)
        }

        binding.team.setOnClickListener {
            findNavController().navigate(AboutFragmentDirections.actionNavigationAboutToNavigationAboutTeam())
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

        binding.textAboutLinkedin.setOnClickListener {
            openUri(context, HTTP_MOVAPP_LINKEDIN)
        }

        binding.textAboutFacebook.setOnClickListener {
            openUri(context, HTTP_MOVAPP_FACEBOOK)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard(view, activity)
    }

}