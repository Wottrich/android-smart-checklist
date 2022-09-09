package wottrich.github.io.tools.base

import kotlin.random.Random

object UuidGenerator {

    fun getRandomUuid(): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString = (1..64)
            .map { i -> Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");
        return randomString
    }

}