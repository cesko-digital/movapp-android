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
import cz.movapp.android.hideKeyboard
import cz.movapp.android.restoreSavableScrollState
import cz.movapp.app.App
import cz.movapp.app.MainViewModel
import cz.movapp.app.adapter.AlphabetAdapter
import cz.movapp.app.databinding.FragmentAlphabetBinding


private val DIRECTION_ARG_KEY = "alphabetDirection"

class AlphabetFragment : Fragment() {

    private var _binding: FragmentAlphabetBinding? = null

    private val binding get() = _binding!!

    private val mainSharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lang = mainSharedViewModel.selectedLanguage.value!!
        val app = this.requireActivity().application as App
        val direction = getArgDirection()
        val viewModel =
            ViewModelProvider(this, AlphabetViewModel.Factory(app, lang, direction))
                .get(direction.name,AlphabetViewModel::class.java)
        _binding = FragmentAlphabetBinding.inflate(inflater, container, false)
        this.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                _binding = null
            }

            override fun onStart(owner: LifecycleOwner) {
                hideKeyboard(binding.root, activity)
            }
        })

        binding.recyclerViewAlphabet.layoutManager = GridLayoutManager(requireContext(), 2)

        viewModel.alphabetsState.observe(viewLifecycleOwner) {
            if(it.isLoaded){
                binding.recyclerViewAlphabet.adapter = AlphabetAdapter(it.alphabetData)
                it.scrollPositions[it.lang.langCode]?.let { scrollPos ->
                    binding.recyclerViewAlphabet.restoreSavableScrollState(scrollPos)
                    (binding.recyclerViewAlphabet.layoutManager as GridLayoutManager)
                        .scrollToPositionWithOffset(scrollPos, 1)
                }
                binding.recyclerViewAlphabet.setHasFixedSize(true)
            }
        }

        this.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                viewModel.onLanguageChanged( oldScrollPosition = binding.recyclerViewAlphabet.getSavableScrollState())
            }
        })
        return binding.root
    }

    private fun getArgDirection(): AlphabetDirection {
        return AlphabetDirection.valueOf(
            requireArguments().getString(DIRECTION_ARG_KEY)!!
        )
    }

    companion object{
        fun newInstance(direction: AlphabetDirection): AlphabetFragment {
            val fragment = AlphabetFragment()
            val args = Bundle()
            args.putString(
                DIRECTION_ARG_KEY,
                direction.name
            )
            fragment.arguments = args

            return fragment
        }
    }
}