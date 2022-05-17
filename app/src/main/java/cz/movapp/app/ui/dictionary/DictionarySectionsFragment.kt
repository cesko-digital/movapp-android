package cz.movapp.app.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cz.movapp.android.hideKeyboard
import cz.movapp.app.MainViewModel
import cz.movapp.app.adapter.DictionarySectionsAdapter
import cz.movapp.app.databinding.FragmentDictionarySectionsBinding

class DictionarySectionsFragment : Fragment() {

    private var _binding: FragmentDictionarySectionsBinding? = null
    private val binding get() = _binding!!

    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels()
    private val mainSharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDictionarySectionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerViewDictionarySections
        recyclerView.setHasFixedSize(true)

        recyclerView.adapter = dictionarySharedViewModel.sections.value

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner, Observer { lang ->
            (binding.recyclerViewDictionarySections.adapter as DictionarySectionsAdapter).langPair = lang
            (binding.recyclerViewDictionarySections.adapter as DictionarySectionsAdapter).notifyDataSetChanged()
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard(view, activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.recyclerViewDictionarySections.adapter = null
        _binding = null
    }
}