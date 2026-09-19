package wottrich.github.io.smartchecklist.testtools

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders

@OptIn(ExperimentalCoroutinesApi::class)
internal val testDispatchers: TestDispatcher = UnconfinedTestDispatcher()

internal fun getMainInjectionRule(
    dispatcher: DispatchersProviders
) = KoinTestRule(dispatcher)

internal fun getDispatchersProvidersToTest(
    testDispatchers: TestDispatcher
): DispatchersProviders {
    return object : DispatchersProviders {
        override val main: CoroutineDispatcher
            get() = testDispatchers
        override val io: CoroutineDispatcher
            get() = testDispatchers
    }
}