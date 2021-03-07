package wottrich.github.io.featurenew.view.task

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.databinding.FragmentTaskListBinding
import wottrich.github.io.featurenew.dialogs.showErrorDialog
import wottrich.github.io.featurenew.view.adapter.TaskAdapter
import wottrich.github.io.featurenew.view.adapter.TaskViewHolderAction
import wottrich.github.io.featurenew.view.task.TaskListViewModel.Companion.ERROR_EMPTY_TASK

class TaskListFragment : Fragment() {

    private val args by navArgs<TaskListFragmentArgs>()
    private val taskListFragment = this

    private val viewModel by viewModel<TaskListViewModel> {
        parametersOf(args.checklistId)
    }
    private lateinit var binding: FragmentTaskListBinding

    private val adapter: TaskAdapter by lazy {
        TaskAdapter(viewModel.tasks.value.orEmpty()).apply {
            setOnTaskAction(::handleOnTaskAction)
        }
    }

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
        setupAdapter()
    }

    private fun setupBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = taskListFragment.viewModel
        }
    }

    private fun setupObservables() {
        viewModel.apply {
            tasks.observe(viewLifecycleOwner) {
                adapter.updateItems(it.orEmpty())
            }

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

    private fun setupAdapter() {
        binding.rvTasks.adapter = adapter
    }

    private fun handleOnTaskAction(action: TaskViewHolderAction) {
        when (action) {
            is TaskViewHolderAction.DeleteTask -> viewModel.deleteTask(action.task)
            is TaskViewHolderAction.CheckTask -> viewModel.updateTask(action.task)
        }
    }

}