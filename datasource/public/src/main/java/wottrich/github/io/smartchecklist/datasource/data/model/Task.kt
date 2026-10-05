package wottrich.github.io.smartchecklist.datasource.data.model

import wottrich.github.io.smartchecklist.uuid.UuidGenerator

/**
 * @param dateCreated epoch millis (persisted format), `0` when unknown.
 */
data class Task(
    override val uuid: String = UuidGenerator.getRandomUuid(),
    override val parentUuid: String,
    override val name: String,
    override val isCompleted: Boolean = false,
    val dateCreated: Long = 0L,
) : TaskContract
