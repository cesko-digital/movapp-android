package cz.movapp.app.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cz.movapp.android.openUri
import cz.movapp.app.BuildConfig
import cz.movapp.app.MainViewModel
import cz.movapp.app.R
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()

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
        const val HTTP_MOVAPP_STAND_BY_UKRAINE =
            "https://www.stojimezaukrajinou.cz"
        const val HTTP_MOVAPP_UMAPA = "https://www.umapa.eu"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)

        val context = this.requireContext()

        setupLanguageSpinner(this, binding.learnChoice)

        binding.textAboutVersion.text = String.format(
            resources.getString(R.string.about_version),
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )

        binding.textAboutWeb.setOnClickListener {
            openUri(context, HTTP_MOVAPP_WEB)
        }

        binding.team.setOnClickListener {
            findNavController().navigate(AboutFragmentDirections.actionNavAboutToNavAboutTeam())
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

        binding.textAboutStandByUkraine.setOnClickListener {
            openUri(context, HTTP_MOVAPP_STAND_BY_UKRAINE)
        }

        binding.textAboutUmapa.setOnClickListener {
            openUri(context, HTTP_MOVAPP_UMAPA)
        }
        return binding.root
    }

    private fun setupLanguageSpinner(fragment: Fragment, spinner: Spinner) {
        val context = spinner.context

        val langItems =
            LanguagePair.values().map { LanguageSpinnerItem(it, context.getString(it.to.adjectiveStringId)) }

        ArrayAdapter(
            context,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            langItems
        ).also { adapter ->
            spinner.adapter = adapter

            mainSharedViewModel.selectedLanguage.observe(fragment.viewLifecycleOwner){ langPair ->
                setSpinnerSelection(
                    spinner,
                    adapter,
                    langItems,
                    langPair
                )
            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mainSharedViewModel.selectLanguage((adapterView?.getItemAtPosition(position) as LanguageSpinnerItem).lang)
                mainSharedViewModel.storeLanguage()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun setSpinnerSelection(
        spinner: Spinner,
        adapter: ArrayAdapter<LanguageSpinnerItem>,
        langItems: List<LanguageSpinnerItem>,
        selectedLangPair: LanguagePair
    ) {
        spinner.setSelection(
            adapter.getPosition(
                toLanguageSpinnerItem(
                    langItems,
                    selectedLangPair
                )
            )
        )
    }

    private fun toLanguageSpinnerItem(
        langPairItems: List<LanguageSpinnerItem>,
        restoredLang: LanguagePair?
    ): LanguageSpinnerItem {
        return (langPairItems.firstOrNull { it.lang == restoredLang }
            ?: langPairItems.first { it.lang == LanguagePair.getDefault() })
    }

    class LanguageSpinnerItem(val lang: LanguagePair, val text: String) {
        override fun toString(): String {
            return text
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}