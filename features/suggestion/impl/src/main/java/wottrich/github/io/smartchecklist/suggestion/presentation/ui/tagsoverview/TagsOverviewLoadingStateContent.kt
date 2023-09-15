package wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagsoverview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
internal fun TagsOverviewLoadingStateContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = SmartChecklistTheme.colors.onPrimary
        )
    }
}