package wottrich.github.io.quicklychecklist.impl.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.Modifier
import wottrich.github.io.baseui.ui.Dimens.BaseFour

@Composable
fun ColumnScope.QuicklyChecklistConfirmBottomSheetContent(
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
                Text("Compartilhar de volta")
            },
            secondaryText = {
                Text("Compartilhe de volta as suas mudanças com a pessoa que te enviou!")
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
                Text("Salvar checklist como nova")
            },
            secondaryText = {
                Text(text = "Salve suas alterações como uma checklist nova!")
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
                Text("Aplicar alteração na checklist existente")
            },
            secondaryText = {
                Text("Identificamos que essa checklist é sua, clique para aplicar as alterações nela!")
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