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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cz.movapp.android.openUri
import cz.movapp.app.BuildConfig
import cz.movapp.app.MainViewModel
import cz.movapp.app.R
import cz.movapp.app.data.Language
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var selectedLanguageObserver: Observer<LanguagePair>? = null

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

        setupNativeLanguageSpinner(this, binding.nativeLanguageChoice)

        mainSharedViewModel.selectedNativeLanguage.observe(viewLifecycleOwner) {
            setupLanguageSpinner(this, binding.learnChoice, it)
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

    private fun setupNativeLanguageSpinner(fragment: Fragment, spinner: Spinner) {
        val context = spinner.context

        val langItems = Language.values().map { NativeLanguageSpinnerItem(it, context.getString(it.stringId)) }

        ArrayAdapter(
            context,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            langItems
        ).also { adapter ->
            spinner.adapter = adapter

            mainSharedViewModel.selectedLanguage.observe(fragment.viewLifecycleOwner){ langPair ->
                setNativeLanguageSpinnerSelection(
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
                selectedLanguageObserver?.let {
                    mainSharedViewModel.selectedLanguage.removeObserver(it)
                }
                val lang = (adapterView?.getItemAtPosition(position) as NativeLanguageSpinnerItem).lang

                if (mainSharedViewModel.selectedLanguage.value!!.from != lang) {
                    mainSharedViewModel.selectLanguage(
                        LanguagePair.values().filter { it.from == lang }[0]
                    )
                }

                mainSharedViewModel.selectNativeLanguage(lang)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun setupLanguageSpinner(fragment: Fragment, spinner: Spinner, nativeLanguage: Language) {
        val context = spinner.context

        var langItems =
            LanguagePair.values().map { LanguageSpinnerItem(it, context.getString(it.to.accusativeStringId)) }

        langItems = if (nativeLanguage.langCode == "uk") {
            langItems.filter { it.lang.to.langCode != nativeLanguage.langCode }
        } else {
            langItems.filter { it.lang.from.langCode == nativeLanguage.langCode }
        }

        ArrayAdapter(
            context,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            langItems
        ).also { adapter ->
            spinner.adapter = adapter

            selectedLanguageObserver = Observer { langPair ->
                setSpinnerSelection(
                    spinner,
                    adapter,
                    langItems,
                    langPair
                )
            }

            mainSharedViewModel.selectedLanguage.observe(
                fragment.viewLifecycleOwner,
                selectedLanguageObserver!!
            )
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

    private fun setNativeLanguageSpinnerSelection(
        spinner: Spinner,
        adapter: ArrayAdapter<NativeLanguageSpinnerItem>,
        langItems: List<NativeLanguageSpinnerItem>,
        selectedLangPair: LanguagePair
    ) {
        spinner.setSelection(
            adapter.getPosition(
                toNativeLanguageSpinnerItem(
                    langItems,
                    selectedLangPair
                )
            )
        )
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

    private fun toNativeLanguageSpinnerItem(
        langPairItems: List<NativeLanguageSpinnerItem>,
        restoredLang: LanguagePair
    ): NativeLanguageSpinnerItem {
        return (langPairItems.firstOrNull { it.lang == restoredLang.from }
            ?: langPairItems.first { it.lang == LanguagePair.getDefault().from })
    }

    private fun toLanguageSpinnerItem(
        langPairItems: List<LanguageSpinnerItem>,
        restoredLang: LanguagePair?
    ): LanguageSpinnerItem {
        return (langPairItems.firstOrNull { it.lang == restoredLang }
            ?: langPairItems.first { it.lang == LanguagePair.getDefault() })
    }

    class NativeLanguageSpinnerItem(val lang: Language, val text: String) {
        override fun toString(): String {
            return text
        }
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