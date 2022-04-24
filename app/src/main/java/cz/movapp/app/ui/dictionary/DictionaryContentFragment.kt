package cz.movapp.app.ui.dictionary

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.MainViewModel
import cz.movapp.app.adapter.DictionaryContentAdapter
import cz.movapp.app.databinding.FragmentDictionaryContentBinding

class DictionaryContentFragment : Fragment() {

    private var _binding: FragmentDictionaryContentBinding? = null
    private var constraint: String = ""
    private lateinit var translationIds: List<String>
    private var favoritesIds = mutableListOf<String>()

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

            translationIds = it.getStringArray("translation_ids")?.toList() ?: listOf<String>()
        }

        if (constraint.isEmpty()) {
            val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDictionaryContentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.recyclerView.setHasFixedSize(true)

        binding.recyclerView.adapter = dictionarySharedViewModel.translations

        (binding.recyclerView.adapter as DictionaryContentAdapter).favoritesIds = favoritesIds

        favoritesViewModel.favorites.observe(viewLifecycleOwner) { it ->
            favoritesIds = mutableListOf<String>()

            it.forEach { favoritesIds.add(it.translationId) }

            (binding.recyclerView.adapter as DictionaryContentAdapter).favoritesIds = favoritesIds

            if (!checkAndShowFavorites() and constraint.isEmpty() and translationIds.isEmpty())
                setEmptyTranslations()
        }

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner) {
            (binding.recyclerView.adapter as DictionaryContentAdapter).langPair =
                mainSharedViewModel.selectedLanguage.value!!
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkAndShowFavorites())
            return

        if (translationIds.isNotEmpty()) {
            (binding.recyclerView.adapter as DictionaryContentAdapter).submitList(
                dictionarySharedViewModel.selectedTranslations(constraint, translationIds)
            )
            return
        }

        if (translationIds.isEmpty() and constraint.isNotEmpty()) {
            (binding.recyclerView.adapter as DictionaryContentAdapter).search(constraint)
            return
        }

        setEmptyTranslations()
    }

    private fun setEmptyTranslations() {
        (binding.recyclerView.adapter as DictionaryContentAdapter).submitList(
            dictionarySharedViewModel.selectedTranslations("", listOf())
        )
    }

    private fun checkAndShowFavorites(): Boolean {
        if (favoritesIds.isEmpty())
            return false

        if (translationIds.isEmpty() and constraint.isEmpty() and favoritesIds.isNotEmpty()) {
            (binding.recyclerView.adapter as DictionaryContentAdapter).submitList(
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