package wottrich.github.io.smartchecklist.testtools

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 17/01/2022
 *
 * Copyright © 2022 AndroidSmartCheckList. All rights reserved.
 *
 */

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutinesTestRule(
    val testDispatchers: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    val dispatchers = object : DispatchersProviders {
        override val main: CoroutineDispatcher
            get() = testDispatchers
        override val io: CoroutineDispatcher
            get() = testDispatchers

    }

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatchers)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }

    fun runBlockingUnitTest(block: suspend TestScope.() -> Unit) =
        runTest(testBody = block)

}