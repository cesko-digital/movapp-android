package cz.movapp.app.ui.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import cz.movapp.android.getSavableScrollState
import cz.movapp.app.App
import cz.movapp.app.MainViewModel
import cz.movapp.app.databinding.FragmentAlphabetBinding


private val DIRECTION_ARG_KEY = "alphabetDirection"

class AlphabetFragment : Fragment() {

    private var _binding: FragmentAlphabetBinding? = null

    private val binding get() = _binding!!

    private val mainSharedViewModel: MainViewModel by activityViewModels()

    private lateinit var alphabetViewModel: AlphabetViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lang = mainSharedViewModel.selectedLanguage.value!!
        val app = this.requireActivity().application as App
        val direction = getArgDirection()
        alphabetViewModel =
            ViewModelProvider(this, AlphabetViewModel.Factory(app, lang, direction))
                .get("${lang.name}_${direction.name}", AlphabetViewModel::class.java)
        _binding = FragmentAlphabetBinding.inflate(inflater, container, false)
        this.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                _binding = null
            }
        })

        binding.recyclerViewAlphabet.layoutManager = GridLayoutManager(requireContext(), 2)

        alphabetViewModel.alphabetsState.observe(viewLifecycleOwner) {
            if(it.isLoaded){
                binding.recyclerViewAlphabet.adapter = AlphabetAdapter(it.alphabetData)
                it.scrollPositions[it.sourceLang.langCode]?.let { scrollPos ->
                    (binding.recyclerViewAlphabet.layoutManager as GridLayoutManager)
                        .scrollToPositionWithOffset(scrollPos, 1)
                }
                binding.recyclerViewAlphabet.setHasFixedSize(true)
            }
        }

        this.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                alphabetViewModel.onLanguageChanged( oldScrollPosition = binding.recyclerViewAlphabet.getSavableScrollState())
            }
        })
        return binding.root
    }

    private fun getArgDirection(): AlphabetDirection {
        return AlphabetDirection.valueOf(
            requireArguments().getString(DIRECTION_ARG_KEY)!!
        )
    }

    override fun onPause() {
        super.onPause()
        alphabetViewModel.store()
    }

    companion object{
        fun newInstance(direction: AlphabetDirection): AlphabetFragment {
            return AlphabetFragment().apply {
                arguments = Bundle().apply {
                    putString(
                        DIRECTION_ARG_KEY,
                        direction.name
                    )
                }
            }
        }
    }
}