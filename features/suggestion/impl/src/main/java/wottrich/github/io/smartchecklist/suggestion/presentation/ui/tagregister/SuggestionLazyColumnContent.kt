package wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagregister

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import wottrich.github.io.smartchecklist.baseui.RowComponent
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.ListItemStartTextContent
import wottrich.github.io.smartchecklist.baseui.ui.RowDefaults
import wottrich.github.io.smartchecklist.suggestion.presentation.ui.TagComponentShape

@Composable
internal fun SuggestionLazyColumnContent(
    modifier: Modifier = Modifier,
    suggestions: List<String>, // TODO improve
) {
    LazyColumn(modifier = modifier.fillMaxWidth().padding(horizontal = Dimens.BaseFour.SizeThree)) {
        items(suggestions) { tagName ->
            Column {
                Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
                RowComponent(
                    modifier = Modifier.clip(TagComponentShape),
                    leftContent = {
                        ListItemStartTextContent(
                            primary = RowDefaults.title(text = tagName)
                        )
                    },
                    rightIconContent = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}