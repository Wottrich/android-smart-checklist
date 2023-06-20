package github.io.wottrich.test.tools

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import github.io.wottrich.coroutines.dispatcher.DispatchersProviders

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
    val testDispatchers: TestCoroutineDispatcher = TestCoroutineDispatcher()
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
        testDispatchers.cleanupTestCoroutines()
    }

    fun runBlockingUnitTest(block: suspend TestCoroutineScope.() -> Unit) =
        testDispatchers.runBlockingTest(block)

}