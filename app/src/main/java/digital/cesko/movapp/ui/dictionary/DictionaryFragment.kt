package digital.cesko.movapp.ui.dictionary

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import digital.cesko.movapp.FavoritesViewModel
import digital.cesko.movapp.FavoritesViewModelFactory
import digital.cesko.movapp.MainViewModel
import digital.cesko.movapp.R
import digital.cesko.movapp.adapter.DictionaryAdapter
import digital.cesko.movapp.data.FavoritesDatabase
import digital.cesko.movapp.databinding.FragmentDictionaryBinding

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val favoritesDatabase: FavoritesDatabase by lazy { FavoritesDatabase.getDatabase(requireContext()) }
    private val favoritesViewModel: FavoritesViewModel by activityViewModels{
        FavoritesViewModelFactory(
            favoritesDatabase.favoritesDao()
        )
    }

    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels{
        DictionaryViewModelFactory(
            requireActivity().application,favoritesViewModel
        )
    }

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
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.recyclerViewDictionary
        dictionarySharedViewModel.sections.observe(viewLifecycleOwner) {
            recyclerView.adapter = it
            recyclerView.setHasFixedSize(true)

            it.fromUa = mainSharedViewModel.fromUa.value == true
        }

        mainSharedViewModel.fromUa.observe(viewLifecycleOwner) {
            (recyclerView.adapter as DictionaryAdapter).fromUa = mainSharedViewModel.fromUa.value == true
            recyclerView.adapter?.notifyDataSetChanged()
        }


        favoritesViewModel.favorites.observe(activity as LifecycleOwner) {
            if (recyclerView.adapter !== null) {
                (recyclerView.adapter as DictionaryAdapter).favorites = it
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}