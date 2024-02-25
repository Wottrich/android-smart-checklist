package wottrich.github.io.smartchecklist.datasource.data.model

import wottrich.github.io.smartchecklist.uuid.UuidGenerator

data class Checklist(
    override val uuid: String = UuidGenerator.getRandomUuid(),
    override val name: String,
    override val isSelected: Boolean = false
) : ChecklistContract