package github.io.wottrich.kotlin

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Implementation of single shot events. Typically used to send actions to be executed by UI just
 * once even after orientation changes
 */
class SingleShotEventBus<T> {
    private val _events = Channel<T>()
    val events = _events.receiveAsFlow() // expose as flow

    suspend fun emit(event: T) {
        _events.send(event) // suspends on buffer overflow
    }
}