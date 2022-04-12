package digital.cesko.movapp.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import digital.cesko.movapp.MainViewModel
import digital.cesko.movapp.adapter.DictionaryContentAdapter
import digital.cesko.movapp.databinding.FragmentDictionaryContentBinding

class DictionaryContentFragment : Fragment() {

    private var _binding: FragmentDictionaryContentBinding? = null
    private var constraint: String = ""
    private var fromUa: Boolean = true
    private lateinit var translationIts: List<String>

    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels()

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

        dictionarySharedViewModel.setSelectedTranslationIds(translationIts)
        dictionarySharedViewModel.search(constraint)
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

        val recyclerView: RecyclerView = binding.recyclerViewDictionaryContent
        dictionarySharedViewModel.translations.observe(viewLifecycleOwner) {
            recyclerView.adapter = it
            recyclerView.setHasFixedSize(true)

            it.fromUa = mainSharedViewModel.fromUa.value == true
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}