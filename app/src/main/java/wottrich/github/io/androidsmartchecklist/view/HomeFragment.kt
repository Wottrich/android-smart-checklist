package wottrich.github.io.androidsmartchecklist.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.androidsmartchecklist.R
import com.example.androidsmartchecklist.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import wottrich.github.io.androidsmartchecklist.ComposeActivity
import wottrich.github.io.androidsmartchecklist.view.adapter.ChecklistAdapter
import wottrich.github.io.database.entity.Checklist
import wottrich.github.io.featurenew.view.NewChecklistActivity
import wottrich.github.io.tools.extensions.startActivity


class HomeFragment : Fragment() {
    
    private val viewModel by sharedViewModel<HomeViewModel>()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinding()
        setupObservables()
        setupRecycler()
        binding.button.setOnClickListener {
            activity?.startActivity<ComposeActivity>()
        }
    }

    private fun setupBinding () {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    private fun setupObservables () {
        viewModel.apply {
            checklists.observe(viewLifecycleOwner) {
                binding.rvChecklists.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun setupRecycler () {
        binding.rvChecklists.apply {
            adapter = ChecklistAdapter(
                requireContext(),
                viewModel.checklists,
                onClick = { checklist ->
                    activity?.let { activity ->
                        checklist.checklistId?.let { id ->
                            NewChecklistActivity.startEditFlow(activity, id)
                        }
                    }
                }
            )
        }
    }

}