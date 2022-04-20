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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                binding.recyclerViewAlphabet.restoreSavableScrollState(it.scrollPositions[it.fromUa]!!)
                binding.recyclerViewAlphabet.setHasFixedSize(true)
            }
        }

        mainSharedViewModel.fromUa.observe(viewLifecycleOwner, Observer { fromUa ->
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


fun RecyclerView.getSavableScrollState(): Int {
    return when (this.layoutManager) {
        null -> throw UnsupportedOperationException("RecyclerView: No LayoutManager set")
        is LinearLayoutManager -> (this.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        else -> throw UnsupportedOperationException("RecyclerView: Can't save scroll state. Unknown LayoutManager")
    }
}

fun RecyclerView.restoreSavableScrollState(position: Int) {
    val layoutManager = this.layoutManager as LinearLayoutManager?
    layoutManager?.scrollToPositionWithOffset(position, 0)
}