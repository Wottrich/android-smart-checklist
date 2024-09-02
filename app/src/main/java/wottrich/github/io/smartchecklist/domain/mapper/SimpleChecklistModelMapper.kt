package wottrich.github.io.smartchecklist.domain.mapper

import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.domain.model.SimpleChecklistModel

class SimpleChecklistModelMapper {
    fun mapToSimpleChecklistModel(checklist: Checklist) =
        SimpleChecklistModel(
            checklist.uuid,
            checklist.name
        )
}