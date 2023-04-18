package wottrich.github.io.tools.base

import java.util.UUID

object UuidGenerator {
    fun getRandomUuid(): String {
        return UUID.randomUUID().toString()
    }
}