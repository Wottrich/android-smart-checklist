package wottrich.github.io.featurenew.view.task

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.databinding.FragmentTaskListBinding
import wottrich.github.io.featurenew.dialogs.showErrorDialog
import wottrich.github.io.featurenew.view.task.TaskListViewModel.Companion.ERROR_EMPTY_TASK

class TaskListFragment : Fragment() {

    private val taskListFragment = this

    private val viewModel by viewModel<TaskListViewModel>()
    private lateinit var binding: FragmentTaskListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_task_list, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinding()
        setupObservables() 
        setupListeners()
    }

    private fun setupBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = taskListFragment.viewModel
        }
    }

    private fun setupObservables() {
        viewModel.apply {
            errorMessage.observe(viewLifecycleOwner) {
                when (it) {
                    ERROR_EMPTY_TASK -> showErrorDialog(R.string.fragment_task_list_error_add_item)
                    else -> showErrorDialog(R.string.unknown)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnAdd.setOnClickListener {
            viewModel.verifyTaskNameToAddItem()
        }
    }

}