package wottrich.github.io.featurenew.view.task

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.databinding.FragmentTaskListBinding
import wottrich.github.io.featurenew.dialogs.showDefaultErrorMessageDialog
import wottrich.github.io.featurenew.dialogs.showErrorDialog
import wottrich.github.io.featurenew.view.adapter.TaskAdapter
import wottrich.github.io.featurenew.view.adapter.TaskViewHolderAction

class TaskListFragment : Fragment() {

    private val args by navArgs<TaskListFragmentArgs>()
    private val taskListFragment = this

    private val viewModel by viewModel<TaskListViewModel> {
        parametersOf(args.checklistId)
    }
    private lateinit var binding: FragmentTaskListBinding

    private val adapter: TaskAdapter by lazy {
        TaskAdapter(listOf()).apply {
            setOnTaskAction(::handleOnTaskAction)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
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
        viewModel.action.observe(viewLifecycleOwner, ::handleAction)
        viewModel.getLiveDataTaskList().observe(viewLifecycleOwner) {
            adapter.updateItems(it)
        }
    }

    private fun handleAction(action: TaskListAction?) {
        when (action) {
            is TaskListAction.UpdateTaskList -> adapter.updateItems(action.taskList)
            is TaskListAction.ErrorMessage -> showErrorDialog(action.stringRes)
            null -> showDefaultErrorMessageDialog()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_check, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.itComplete) {
            activity?.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}