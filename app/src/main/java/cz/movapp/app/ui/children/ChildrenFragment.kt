package cz.movapp.app.ui.children

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cz.movapp.app.MainActivity
import cz.movapp.app.MainViewModel
import cz.movapp.app.adapter.ChildrenAdapter
import cz.movapp.app.databinding.FragmentChildrenBinding

class ChildrenFragment : Fragment() {

    private var _binding: FragmentChildrenBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val childrenViewModel: ChildrenViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).setupTopAppBarWithSearchWithMenu()

        _binding = FragmentChildrenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerViewChildren
        childrenViewModel.children.observe(viewLifecycleOwner) {
            it.langPair = mainSharedViewModel.selectedLanguage.value!!
            recyclerView.adapter = it
            recyclerView.setHasFixedSize(true)

        }

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner) {
            (recyclerView.adapter as ChildrenAdapter).langPair = mainSharedViewModel.selectedLanguage.value!!
            recyclerView.adapter?.notifyDataSetChanged()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewChildren.adapter = null
        _binding = null
    }
}