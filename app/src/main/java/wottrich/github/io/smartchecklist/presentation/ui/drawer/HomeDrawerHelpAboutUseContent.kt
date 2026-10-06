package wottrich.github.io.smartchecklist.presentation.ui.drawer

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import wottrich.github.io.smartchecklist.R.string
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun HelpAboutUsContent(
    onBackupClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onHelpClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        DrawerBottomTextButton(label = string.drawer_bottom_items_backup, onClick = onBackupClick)
        DrawerBottomTextButton(label = string.drawer_bottom_items_about_us, onClick = onAboutUsClick)
        DrawerBottomTextButton(label = string.drawer_bottom_items_help, onClick = onHelpClick)
    }
}

@Composable
private fun RowScope.DrawerBottomTextButton(
    @StringRes label: Int,
    onClick: () -> Unit,
) {
    TextButton(
        modifier = Modifier.weight(1f),
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = SmartChecklistTheme.colors.onPrimary)
    ) {
        Text(text = stringResource(id = label))
    }
}
