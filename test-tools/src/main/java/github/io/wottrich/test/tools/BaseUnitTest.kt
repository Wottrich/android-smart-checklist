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

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseUnitTest {

    @Before
    open fun setUp() {}

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

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