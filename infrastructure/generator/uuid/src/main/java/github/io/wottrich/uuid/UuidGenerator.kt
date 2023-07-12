package github.io.wottrich.uuid

import java.util.UUID

object UuidGenerator {
    fun getRandomUuid(): String {
        return UUID.randomUUID().toString()
    }
}