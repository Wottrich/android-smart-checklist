package github.io.wottrich.test.tools

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Before
import org.junit.Rule
import wottrich.github.io.tools.dispatcher.DispatchersProviders

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 17/01/2022
 *
 * Copyright © 2022 AndroidSmartCheckList. All rights reserved.
 *
 */

private val testDispatchers: TestCoroutineDispatcher = TestCoroutineDispatcher()

private fun getMainInjectionRule(
    dispatcher: DispatchersProviders
) = KoinTestRule(dispatcher)

private fun getDispatchersProvidersToTest(
    testDispatchers: TestCoroutineDispatcher
): DispatchersProviders {
    return object : DispatchersProviders {
        override val main: CoroutineDispatcher
            get() = testDispatchers
        override val io: CoroutineDispatcher
            get() = testDispatchers
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseUnitTest(
    private val dispatchersProviders: DispatchersProviders = getDispatchersProvidersToTest(testDispatchers),
    injectionTestRuleImpl: InjectionTestRule = getMainInjectionRule(dispatchersProviders)
) {

    @Before
    open fun setUp() {}

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule(testDispatchers)

    @get:Rule
    val injectionTestRule = injectionTestRuleImpl

    fun runBlockingUnitTest(block: suspend TestCoroutineScope.() -> Unit) =
        coroutinesTestRule.runBlockingUnitTest(block)

    fun <T> getSuspendValue(block: suspend TestCoroutineScope.() -> T): T {
        var value: T? = null
        coroutinesTestRule.runBlockingUnitTest {
            value = block()
        }
        return checkNotNull(value)
    }

}