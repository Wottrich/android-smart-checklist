package wottrich.github.io.smartchecklist.datasource.data.model

import wottrich.github.io.smartchecklist.uuid.UuidGenerator

data class Task(
    override val uuid: String = UuidGenerator.getRandomUuid(),
    override val parentUuid: String,
    override val name: String,
    override val isCompleted: Boolean = false
) : TaskContract