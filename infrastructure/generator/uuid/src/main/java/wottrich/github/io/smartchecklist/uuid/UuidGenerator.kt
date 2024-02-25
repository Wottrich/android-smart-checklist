package wottrich.github.io.smartchecklist.uuid

import java.util.UUID

object UuidGenerator {
    fun getRandomUuid(): String {
        return UUID.randomUUID().toString()
    }
}