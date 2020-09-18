package wottrich.github.io.featurenew.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.databinding.FragmentNewChecklistBinding
import wottrich.github.io.featurenew.dialogs.showErrorDialog
import wottrich.github.io.featurenew.view.NewChecklistViewModel.Companion.ERROR_CONTINUE

class NewChecklistFragment : Fragment() {

    private val viewModel by viewModel<NewChecklistViewModel>()
    private lateinit var binding: FragmentNewChecklistBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_new_checklist, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservables()
    }

    private fun setupObservables() = viewModel.apply {
        navigation.observe(viewLifecycleOwner) {

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