package wottrich.github.io.smartchecklist.domain.mapper

import wottrich.github.io.smartchecklist.presentation.ui.model.SimpleChecklistModel
import wottrich.github.io.smartchecklist.datasource.entity.NewChecklist

class SimpleChecklistModelMapper {
    fun mapToSimpleChecklistModel(newChecklist: NewChecklist) =
        SimpleChecklistModel(
            newChecklist.uuid,
            newChecklist.name
        )
}