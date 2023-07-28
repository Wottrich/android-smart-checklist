package wottrich.github.io.smartchecklist.testtools

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import wottrich.github.io.smartchecklist.testtools.InjectionTestRule

class KoinTestRule(
    val dispatchersProviders: DispatchersProviders
) : InjectionTestRule() {
    override fun startInjection() {
        startKoin {
            modules(testModule)
        }
    }

    override fun closeInjection() {
        stopKoin()
    }

    private val testModule = module {
        single<DispatchersProviders> { dispatchersProviders }
    }
}