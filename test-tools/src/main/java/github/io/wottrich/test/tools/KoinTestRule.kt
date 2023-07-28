package github.io.wottrich.test.tools

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders

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