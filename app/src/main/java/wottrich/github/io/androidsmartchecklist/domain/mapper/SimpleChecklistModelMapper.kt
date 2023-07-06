package wottrich.github.io.androidsmartchecklist.domain.mapper

import wottrich.github.io.androidsmartchecklist.presentation.ui.model.SimpleChecklistModel
import wottrich.github.io.datasource.entity.NewChecklist

class SimpleChecklistModelMapper {
    fun mapToSimpleChecklistModel(newChecklist: NewChecklist) =
        SimpleChecklistModel(
            newChecklist.uuid,
            newChecklist.name
        )
}