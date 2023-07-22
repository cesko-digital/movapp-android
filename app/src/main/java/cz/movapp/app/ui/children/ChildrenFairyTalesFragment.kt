package cz.movapp.app.ui.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.activityViewModels
import cz.movapp.app.MainViewModel
import cz.movapp.app.databinding.FragmentChildrenFairyTalesSelectionBinding

class ChildrenFairyTalesFragment : Fragment() {

    private var _binding: FragmentChildrenFairyTalesSelectionBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val childrenFairyTalesViewModel: ChildrenFairyTalesViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChildrenFairyTalesSelectionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerViewChildrenFairyTales

        childrenFairyTalesViewModel.fairyTales.observe(viewLifecycleOwner) {
            it.langPair = mainSharedViewModel.selectedLanguage.value!!
            it.setSupportedDataset()
            recyclerView.adapter = it
            recyclerView.setHasFixedSize(true)

            childrenFairyTalesViewModel.fairyTales.value?.onItemClicked = { slug ->
                findNavController().navigate(
                    ChildrenFragmentDirections.toFairyTalePlayer(slug)
                )
            }
        }

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner) {
            (recyclerView.adapter as ChildrenFairyTalesAdapter).langPair = it
            recyclerView.adapter?.notifyDataSetChanged()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewChildrenFairyTales.adapter = null
        _binding = null
    }
}