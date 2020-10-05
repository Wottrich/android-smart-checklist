package wottrich.github.io.featurenew.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.databinding.FragmentChecklistNameBinding
import wottrich.github.io.featurenew.dialogs.showErrorDialog
import wottrich.github.io.featurenew.view.ChecklistNameViewModel.Companion.ERROR_CONTINUE

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
            viewModel = checklistNameFragment.viewModel
        }
    }

    private fun setupObservables() = viewModel.apply {
        navigation.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "work", Toast.LENGTH_SHORT).show()
        }

        errorMessage.observe(viewLifecycleOwner) {
            when(it) {
                ERROR_CONTINUE -> showErrorDialog(R.string.fragment_new_checklist_error_continue)
                else -> showErrorDialog(R.string.unknown)
            }
        }
    }

    private fun setupListeners() {
        binding.btnContinue.setOnClickListener {
            viewModel.nextScreen()
        }
    }

}