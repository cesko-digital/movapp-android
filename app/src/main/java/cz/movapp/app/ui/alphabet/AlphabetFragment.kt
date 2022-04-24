package cz.movapp.app.ui.alphabet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import cz.movapp.android.getSavableScrollState
import cz.movapp.android.restoreSavableScrollState
import cz.movapp.app.App
import cz.movapp.app.MainViewModel
import cz.movapp.app.adapter.AlphabetAdapter
import cz.movapp.app.appModule
import cz.movapp.app.databinding.FragmentAlphabetBinding


class AlphabetFragment : Fragment() {

    private var _binding: FragmentAlphabetBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mainSharedViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val app = this.requireActivity().application as App
        val appModule = app.appModule()
        val viewModel =
            ViewModelProvider(this, AlphabetViewModel.Factory(app, mainSharedViewModel))
                .get(AlphabetViewModel::class.java)
        _binding = FragmentAlphabetBinding.inflate(inflater, container, false)

        binding.recyclerViewAlphabet.layoutManager = GridLayoutManager(requireContext(), 2)

        viewModel.alphabetsState.observe(viewLifecycleOwner) {
            if(it.isLoaded){
                binding.recyclerViewAlphabet.adapter = AlphabetAdapter(it.alphabetData)
                it.scrollPositions[it.lang.from.langCode]?.let { scrollPos ->
                    binding.recyclerViewAlphabet.restoreSavableScrollState(scrollPos)
                }
                binding.recyclerViewAlphabet.setHasFixedSize(true)
            }
        }

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner, Observer { fromUa ->
            viewModel.onLanguageChanged(fromUa, binding.recyclerViewAlphabet.getSavableScrollState())
        })

        this.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                viewModel.onLanguageChanged( oldScrollPosition = binding.recyclerViewAlphabet.getSavableScrollState())
            }
        })

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}