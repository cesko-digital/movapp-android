package digital.cesko.movapp.ui.alphabet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import digital.cesko.movapp.MainViewModel
import digital.cesko.movapp.R
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
        val languageStr = when (mainSharedViewModel.fromUa.value == true) {
            true -> "uk"
            false -> "cs"
        }

        val alphabetViewModel =
            ViewModelProvider(this, AlphabetViewModelFactory(requireActivity().application, languageStr)).get(AlphabetViewModel::class.java)

        _binding = FragmentAlphabetBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.recyclerViewAlphabet
        alphabetViewModel.alphabet.observe(viewLifecycleOwner) {
            recyclerView.adapter = it
            recyclerView.setHasFixedSize(true)
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}