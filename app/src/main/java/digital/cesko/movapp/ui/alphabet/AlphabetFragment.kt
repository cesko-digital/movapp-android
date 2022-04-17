package digital.cesko.movapp.ui.alphabet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import digital.cesko.movapp.MainViewModel
import digital.cesko.movapp.databinding.FragmentAlphabetBinding

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

        val viewModel =
            ViewModelProvider(this, AlphabetViewModel.Factory(this.requireActivity().application, mainSharedViewModel))
                .get(AlphabetViewModel::class.java)
        _binding = FragmentAlphabetBinding.inflate(inflater, container, false)


        mainSharedViewModel.fromUa.observe(viewLifecycleOwner, Observer { fromUa ->
            viewModel.setCurrentAlphabet(fromUa)
        })

        viewModel.currentAlphabet.observe(viewLifecycleOwner) {
            binding.recyclerViewAlphabet.adapter = it
            binding.recyclerViewAlphabet.setHasFixedSize(true)
        }

        binding.recyclerViewAlphabet.layoutManager = GridLayoutManager(requireContext(), 2)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


