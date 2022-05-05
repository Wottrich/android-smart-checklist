package github.io.wottrich.test.tools

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Before
import org.junit.Rule

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 17/01/2022
 *
 * Copyright © 2022 AndroidSmartCheckList. All rights reserved.
 *
 */

private val MAIN_INJECTION_RULE = KoinTestRule()

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseUnitTest(injectionTestRuleImpl: InjectionTestRule = MAIN_INJECTION_RULE) {

    @Before
    open fun setUp() {}

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

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