package digital.cesko.movapp.ui.dictionary

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import digital.cesko.movapp.FavoritesViewModel
import digital.cesko.movapp.MainViewModel
import digital.cesko.movapp.R
import digital.cesko.movapp.adapter.DictionaryContentAdapter
import digital.cesko.movapp.databinding.FragmentDictionaryContentBinding

class DictionaryContentFragment : Fragment() {

    private var _binding: FragmentDictionaryContentBinding? = null
    private var constraint: String = ""
    private lateinit var translationIts: List<String>
    private var favoritesIds = mutableListOf<String>()

    private lateinit var recyclerView: RecyclerView

    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels()
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            // constraint can be sectionId or search string
            constraint = it.getString("constraint").toString()

            translationIts = it.getStringArray("translation_ids")?.toList() ?: listOf<String>()
        }

        if (constraint.isEmpty()) {
            val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)

            requireActivity().findViewById<TextInputLayout>(R.id.input_layout_search)?.clearFocus()
            requireActivity().findViewById<TextInputEditText>(R.id.input_search)?.text?.clear()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val viewModel =
        //    ViewModelProvider(this, DictionaryViewModelFactory(requireActivity().application, constraint, translationIds)).get(DictionaryViewModel::class.java)

        _binding = FragmentDictionaryContentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.recyclerViewDictionaryContent
        recyclerView.setHasFixedSize(true)

        recyclerView.adapter = dictionarySharedViewModel.translations

        (recyclerView.adapter as DictionaryContentAdapter).favoritesIds = favoritesIds

        favoritesViewModel.favorites.observe(viewLifecycleOwner) { it ->
            favoritesIds = mutableListOf<String>()

            it.forEach { favoritesIds.add(it.translationId) }

            (recyclerView.adapter as DictionaryContentAdapter).favoritesIds = favoritesIds

            checkAndShowFavorites()
        }

        mainSharedViewModel.fromUa.observe(viewLifecycleOwner) {
            (recyclerView.adapter as DictionaryContentAdapter).fromUa = mainSharedViewModel.fromUa.value == true
            recyclerView.adapter?.notifyDataSetChanged()
        }

        dictionarySharedViewModel.currentSectionTitle.observe(viewLifecycleOwner) {
            label -> (activity as AppCompatActivity).supportActionBar?.title = label
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkAndShowFavorites())
            return

        if (translationIts.isNotEmpty())
            (recyclerView.adapter as DictionaryContentAdapter).submitList(
                dictionarySharedViewModel.selectedTranslations(constraint, translationIts)
            )

        if (translationIts.isEmpty() and constraint.isNotEmpty())
            (recyclerView.adapter as DictionaryContentAdapter).search(constraint)
    }

    private fun checkAndShowFavorites(): Boolean {
        if (favoritesIds.isEmpty())
            return false

        if (translationIts.isEmpty() and constraint.isEmpty()) {
            (recyclerView.adapter as DictionaryContentAdapter).submitList(
                dictionarySharedViewModel.selectedTranslations(constraint, favoritesIds)
            )

            return true
        }

        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}