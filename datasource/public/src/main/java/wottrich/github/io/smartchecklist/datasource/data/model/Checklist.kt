package wottrich.github.io.smartchecklist.datasource.data.model

import wottrich.github.io.smartchecklist.uuid.UuidGenerator

/**
 * @param createdDate epoch millis (persisted format), `0` when unknown.
 * @param lastUpdate epoch millis (persisted format), `0` when unknown.
 */
data class Checklist(
    override val uuid: String = UuidGenerator.getRandomUuid(),
    override val parentUuid: String? = null,
    override val name: String,
    override val isSelected: Boolean = false,
    val createdDate: Long = 0L,
    val lastUpdate: Long = 0L,
) : ChecklistContract
