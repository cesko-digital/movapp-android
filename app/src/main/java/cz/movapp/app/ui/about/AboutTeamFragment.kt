package cz.movapp.app.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cz.movapp.android.hideKeyboard
import cz.movapp.app.MainActivity
import cz.movapp.app.R
import cz.movapp.app.databinding.FragmentAboutTeamBinding

class AboutTeamFragment : Fragment() {

    private var _binding: FragmentAboutTeamBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutTeamBinding.inflate(inflater, container, false)

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                binding.scrollView.removeAllViews()
                _binding = null
            }
        })

        val context = this.requireContext()
        val mainActivity = requireActivity() as MainActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard(view, activity)
    }

}