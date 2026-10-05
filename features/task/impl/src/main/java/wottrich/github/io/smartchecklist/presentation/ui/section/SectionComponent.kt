package wottrich.github.io.smartchecklist.presentation.ui.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.RowDefaults
import wottrich.github.io.smartchecklist.baseui.ui.TextStateComponent
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun SectionComponent(
    sectionName: String,
    isSectionEmpty: Boolean
) {
    val shape = if (isSectionEmpty) {
        RoundedCornerShape(Dimens.BaseFour.SizeOne)
    } else {
        RoundedCornerShape(topStart = Dimens.BaseFour.SizeOne, topEnd = Dimens.BaseFour.SizeOne)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.BaseFour.SizeOne)
            .padding(horizontal = Dimens.BaseFour.SizeOne)
            .clip(shape)
            .background(SmartChecklistTheme.colors.surface),
    ) {
        TextStateComponent(
            modifier = Modifier.padding(Dimens.BaseFour.SizeTwo),
            textState = RowDefaults.title(
                sectionName,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun SectionTaskComponentSurface(isLastTask: Boolean, content: @Composable () -> Unit) {
    val modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = Dimens.BaseFour.SizeOne)

    val shape = if (isLastTask) {
        Modifier
            .padding(bottom = Dimens.BaseFour.SizeOne)
            .clip(RoundedCornerShape(bottomStart = Dimens.BaseFour.SizeOne, bottomEnd = Dimens.BaseFour.SizeOne))
    } else Modifier
    Column(
        modifier = modifier
            .then(shape)
            .background(SmartChecklistTheme.colors.surface),
    ) {
        val modifier = if (isLastTask) Modifier.padding(bottom = Dimens.BaseFour.SizeOne)
        else Modifier
        Box(
            modifier = modifier
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun SectionComponentPreview() {
    ApplicationTheme(isSystemInDarkTheme = true) {
        Scaffold {
            Box(modifier = Modifier.padding(it)) {
                SectionComponent("Frutas", isSectionEmpty = false)
            }
        }
    }
}