package wottrich.github.io.smartchecklist.newchecklist.domain.model

import wottrich.github.io.smartchecklist.uuid.UuidGenerator

data class NewChecklistModel(
    val uuid: String = UuidGenerator.getRandomUuid(),
    val parentUuid: String? = null,
    val name: String
)