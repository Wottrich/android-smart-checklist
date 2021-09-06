package wottrich.github.io.featurenew.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.databinding.FragmentChecklistNameBinding
import wottrich.github.io.featurenew.dialogs.showDefaultErrorMessageDialog
import wottrich.github.io.featurenew.dialogs.showErrorDialog

class ChecklistNameFragment : Fragment() {

    private val checklistNameFragment = this

    private val viewModel by viewModel<ChecklistNameViewModel>()
    private lateinit var binding: FragmentChecklistNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_checklist_name, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservables()
        setupBinding()
    }

    private fun setupBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            //viewModel = checklistNameFragment.viewModel
        }
    }

    private fun setupObservables() = viewModel.apply {
        action.observe(viewLifecycleOwner) {
            when (it) {
                is ChecklistNameAction.NextScreen -> onNextScreen(it)
                is ChecklistNameAction.ErrorMessage -> showErrorDialog(it.stringRes)
                else -> showDefaultErrorMessageDialog()
            }
        }
    }

    private fun onNextScreen(nextScreen: ChecklistNameAction.NextScreen) {
//        val direction = ChecklistNameFragmentDirections.actionChecklistNameFragmentToTaskListFragment(nextScreen.checklistId)
//        findNavController().navigate(direction)
    }

    private fun setupListeners() {
//        binding.btnContinue.setOnClickListener {
//            viewModel.nextScreen()
//        }
    }

}