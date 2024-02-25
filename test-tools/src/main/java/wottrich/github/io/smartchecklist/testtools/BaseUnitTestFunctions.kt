package wottrich.github.io.smartchecklist.testtools

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders

internal val testDispatchers: TestCoroutineDispatcher = TestCoroutineDispatcher()

internal fun getMainInjectionRule(
    dispatcher: DispatchersProviders
) = KoinTestRule(dispatcher)

internal fun getDispatchersProvidersToTest(
    testDispatchers: TestCoroutineDispatcher
): DispatchersProviders {
    return object : DispatchersProviders {
        override val main: CoroutineDispatcher
            get() = testDispatchers
        override val io: CoroutineDispatcher
            get() = testDispatchers
    }
}