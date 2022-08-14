package cz.movapp.app.ui.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import cz.movapp.android.restoreSavableScrollState
import cz.movapp.app.MainViewModel
import cz.movapp.app.databinding.FragmentChildrenDictionaryBinding

class ChildrenDictionaryFragment : Fragment() {

    private var _binding: FragmentChildrenDictionaryBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val childrenDictionaryViewModel: ChildrenDictionaryViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChildrenDictionaryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerViewChildren
        childrenDictionaryViewModel.children.observe(viewLifecycleOwner) {
            it.langPair = mainSharedViewModel.selectedLanguage.value!!
            recyclerView.adapter = it
            recyclerView.setHasFixedSize(true)

        }

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner) {
            childrenDictionaryViewModel.onLanguageChanged(it)
            (recyclerView.adapter as ChildrenDictionaryAdapter).langPair = it
            recyclerView.adapter?.notifyDataSetChanged()
        }

        childrenDictionaryViewModel.childrenState.observe(viewLifecycleOwner) {
            it.let { scrollPos ->
                if (scrollPos != null) {
                    binding.recyclerViewChildren.restoreSavableScrollState(scrollPos)
                }
            }
        }

        this.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                childrenDictionaryViewModel.storeState(
                    (binding.recyclerViewChildren.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                )
            }
        })

        return root
    }

    override fun onPause() {
        super.onPause()

        childrenDictionaryViewModel.store()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewChildren.adapter = null
        _binding = null
    }
}