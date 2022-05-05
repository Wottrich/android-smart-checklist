package github.io.wottrich.test.tools

import org.junit.rules.TestWatcher
import org.junit.runner.Description

abstract class InjectionTestRule : TestWatcher() {
    abstract fun startInjection()
    abstract fun closeInjection()

    override fun starting(description: Description?) {
        super.starting(description)
        startInjection()
    }

    override fun finished(description: Description?) {
        super.finished(description)
        closeInjection()
    }
}