package digital.cesko.movapp.ui.children

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import digital.cesko.movapp.MainViewModel
import digital.cesko.movapp.R
import digital.cesko.movapp.adapter.ChildrenAdapter
import digital.cesko.movapp.databinding.FragmentChildrenBinding

class ChildrenFragment : Fragment() {

    private var _binding: FragmentChildrenBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val childrenViewModel: ChildrenViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        _binding = FragmentChildrenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.recyclerViewChildren
        childrenViewModel.children.observe(viewLifecycleOwner) {
            recyclerView.adapter = it
            recyclerView.setHasFixedSize(true)

            it.fromUa = mainSharedViewModel.fromUa.value == true
        }

        mainSharedViewModel.fromUa.observe(viewLifecycleOwner) {
            (recyclerView.adapter as ChildrenAdapter).fromUa = mainSharedViewModel.fromUa.value == true
            recyclerView.adapter?.notifyDataSetChanged()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}