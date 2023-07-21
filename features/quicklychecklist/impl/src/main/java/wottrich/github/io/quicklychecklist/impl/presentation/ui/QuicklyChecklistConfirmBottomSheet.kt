package wottrich.github.io.quicklychecklist.impl.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.baseui.ui.ApplicationTheme
import wottrich.github.io.baseui.ui.Dimens.BaseFour
import wottrich.github.io.androidsmartchecklist.quicklychecklist.R
import wottrich.github.io.androidsmartchecklist.quicklychecklist.R.string
import wottrich.github.io.quicklychecklist.impl.presentation.states.QuicklyChecklistConfirmUiEffect.FailureShareChecklistBack
import wottrich.github.io.quicklychecklist.impl.presentation.states.QuicklyChecklistConfirmUiEffect.OnSaveNewChecklist
import wottrich.github.io.quicklychecklist.impl.presentation.states.QuicklyChecklistConfirmUiEffect.OnShareChecklistBack
import wottrich.github.io.quicklychecklist.impl.presentation.viewmodels.QuicklyChecklistConfirmViewModel

@Composable
fun QuicklyChecklistConfirmBottomSheetContent(
    quicklyChecklistJson: String,
    hasExistentChecklist: Boolean,
    onShareBackClick: (quicklyChecklistJson: String) -> Unit,
    onSaveChecklist: (quicklyChecklistJson: String) -> Unit,
    onReplaceExistentChecklist: () -> Unit,
    viewModel: QuicklyChecklistConfirmViewModel = getViewModel {
        parametersOf(quicklyChecklistJson)
    },
) {
    ApplicationTheme {
        Effects(
            viewModel = viewModel,
            onShareBackClick = onShareBackClick,
            onSaveChecklist = onSaveChecklist
        )
        Screen(
            hasExistentChecklist = hasExistentChecklist,
            onShareBackClick = {
                viewModel.onShareChecklistBackClick()
            },
            onSaveChecklist = {
                viewModel.onSaveNewChecklistClick()
            },
            onReplaceExistentChecklist = onReplaceExistentChecklist
        )
    }
}

@Composable
private fun Effects(
    viewModel: QuicklyChecklistConfirmViewModel,
    onShareBackClick: (quicklyChecklistJson: String) -> Unit,
    onSaveChecklist: (quicklyChecklistJson: String) -> Unit
) {
    val effects = viewModel.effects
    val context = LocalContext.current
    LaunchedEffect(key1 = effects) {
        effects.collect { effect ->
            when (effect) {
                is OnSaveNewChecklist -> onSaveChecklist(effect.quicklyChecklistJson)
                is OnShareChecklistBack -> onShareBackClick(effect.encodedQuicklyChecklist)
                is FailureShareChecklistBack -> Toast.makeText(
                    context,
                    context.getString(R.string.quickly_checklist_share_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

@Composable
private fun Screen(
    hasExistentChecklist: Boolean,
    onShareBackClick: () -> Unit,
    onSaveChecklist: () -> Unit,
    onReplaceExistentChecklist: () -> Unit
) {
    LazyColumn {
        shareBack(onShareBackClick)
        saveChecklist(onSaveChecklist)
        if (hasExistentChecklist) {
            replaceExistentChecklist(onReplaceExistentChecklist)
        }
    }
}

private fun LazyListScope.shareBack(onClick: () -> Unit) {
    item {
        ItemContent(
            listItemModifier = Modifier.padding(bottom = BaseFour.SizeThree),
            icon = {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null
                )
            },
            text = {
                Text(stringResource(id = string.quickly_checklist_share_back))
            },
            secondaryText = {
                Text(stringResource(id = string.quickly_checklist_share_back_description))
            },
            onClick = onClick
        )
    }
}


private fun LazyListScope.saveChecklist(onClick: () -> Unit) {
    item {
        ItemContent(
            icon = {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = null
                )
            },
            text = {
                Text(stringResource(id = string.quickly_checklist_save_new_checklist))
            },
            secondaryText = {
                Text(text = stringResource(id = string.quickly_checklist_save_new_checklist_description))
            },
            onClick = onClick
        )
    }
}

private fun LazyListScope.replaceExistentChecklist(onClick: () -> Unit) {
    item {
        ItemContent(
            listItemModifier = Modifier.padding(bottom = BaseFour.SizeThree),
            icon = {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = null
                )
            },
            text = {
                Text(stringResource(id = string.quickly_checklist_save_existent_checklist))
            },
            secondaryText = {
                Text(stringResource(id = string.quickly_checklist_save_existent_checklist_description))
            },
            onClick = onClick
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ItemContent(
    listItemModifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    secondaryText: @Composable () -> Unit = {},
    onClick: () -> Unit
) {
    Column(modifier = Modifier
        .clickable { onClick() }
        .fillMaxWidth()) {
        ListItem(
            modifier = listItemModifier,
            icon = icon,
            text = text,
            secondaryText = secondaryText
        )
        Divider(modifier = Modifier.fillMaxWidth())
    }
}