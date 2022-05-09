package cz.movapp.app.ui.about

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cz.movapp.android.StateStore
import cz.movapp.android.hideKeyboard
import cz.movapp.android.openUri
import cz.movapp.app.BuildConfig
import cz.movapp.app.R
import cz.movapp.app.appModule
import cz.movapp.app.data.Language
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.databinding.FragmentAboutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

val SELECTED_LANG = StateStore.Key<Language>("PREFLANGUAGE")

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
                _binding = null
            }
        })

        val context = this.requireContext()

        setupLanguageSpinner(context, binding.learnChoice)

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

    private fun setupLanguageSpinner(context: Context, spinner: Spinner) {
        val langItems =
            Language.values().map { LanguageSpinnerItem(it, context.getString(it.adjectiveStringId)) }

        ArrayAdapter(
            context,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            langItems
        ).also { adapter ->
            spinner.adapter = adapter
            lifecycleScope.launch(Dispatchers.Main) {
                val lang = appModule().stateStore.restoreState(SELECTED_LANG)
                    .flowOn(Dispatchers.IO)
                    .collect { restoredLang ->
                        spinner.setSelection(
                            adapter.getPosition(
                                toLanguageSpinnerItem(
                                    langItems,
                                    restoredLang
                                )
                            )
                        )
                    }
            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                appModule().stateStore.saveState(
                    SELECTED_LANG,
                    (adapterView?.getItemAtPosition(position) as LanguageSpinnerItem).lang
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun toLanguageSpinnerItem(
        langItems: List<LanguageSpinnerItem>,
        restoredLang: Language?
    ): LanguageSpinnerItem {
        return (langItems.firstOrNull { it.lang == restoredLang }
            ?: langItems.first { it.lang == LanguagePair.CsToUk.from })
    }

    class LanguageSpinnerItem(val lang: Language, val text: String) {
        override fun toString(): String {
            return text
        }
    }

    fun Fragment.appModule() = this.requireActivity().application.appModule()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard(view, activity)
    }

}