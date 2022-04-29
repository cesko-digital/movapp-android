package cz.movapp.app.ui.dictionary

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
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.FavoritesViewModelFactory
import cz.movapp.app.MainActivity
import cz.movapp.app.MainViewModel
import cz.movapp.app.adapter.DictionaryAdapter
import cz.movapp.app.data.FavoritesDatabase
import cz.movapp.app.databinding.FragmentDictionaryBinding

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

        val recyclerView = binding.recyclerViewDictionary
        recyclerView.setHasFixedSize(true)
        dictionarySharedViewModel.sections.observe(viewLifecycleOwner) {
            recyclerView.adapter = it

            it.langPair = mainSharedViewModel.selectedLanguage.value!!
        }

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner) {
            (recyclerView.adapter as DictionaryAdapter).langPair = mainSharedViewModel.selectedLanguage.value!!
            recyclerView.adapter?.notifyDataSetChanged()
        }

        setupTopAppBarWithSearchWithMenu()

        return root
    }

    /**
     * Activity binding is not init when application starts
     * thus postponing it to onStart when app starts/binding is null, otherwise do it right away
     */
    private fun setupTopAppBarWithSearchWithMenu() {
        val mainActivity = requireActivity() as MainActivity
        try {
            mainActivity.setupTopAppBarWithSearchWithMenu()
        } catch (e: UninitializedPropertyAccessException) {
            lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    //must be in onStart otherwise activity binding not init yet
                    mainActivity.setupTopAppBarWithSearchWithMenu()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewDictionary.adapter = null
        _binding = null
    }
}