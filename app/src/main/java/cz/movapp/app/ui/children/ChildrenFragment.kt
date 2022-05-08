package cz.movapp.app.ui.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cz.movapp.android.hideKeyboard
import cz.movapp.app.MainActivity
import cz.movapp.app.MainViewModel
import cz.movapp.app.adapter.ChildrenAdapter
import cz.movapp.app.data.SharedPrefsRepository
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
        _binding = FragmentChildrenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val pref = SharedPrefsRepository(requireContext())

        val recyclerView = binding.recyclerViewChildren
        childrenViewModel.children.observe(viewLifecycleOwner) {
            it.preferedLanguage = pref.getPreferedLanguage()!!
            recyclerView.adapter = it
            recyclerView.setHasFixedSize(true)

        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard(view, activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewChildren.adapter = null
        _binding = null
    }
}