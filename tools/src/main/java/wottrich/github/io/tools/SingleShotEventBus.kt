package wottrich.github.io.tools

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Implementation of single shot events. Typically used to send actions to be executed by UI just
 * once even after orientation changes
 */
class SingleShotEventBus<T> {

    private val _events = Channel<T>(Channel.BUFFERED)
    val events: Flow<T> = _events.receiveAsFlow()

    suspend fun postEvent(event: T) {
        _events.send(event)
    }

}