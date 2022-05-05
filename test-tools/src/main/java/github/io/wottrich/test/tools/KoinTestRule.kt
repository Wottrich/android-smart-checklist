package github.io.wottrich.test.tools

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import wottrich.github.io.tools.dispatcher.DispatchersProviders

class KoinTestRule(
    val testDispatchers: TestCoroutineDispatcher = TestCoroutineDispatcher()
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
        single<DispatchersProviders> {
            object : DispatchersProviders {
                override val main: CoroutineDispatcher
                    get() = testDispatchers
                override val io: CoroutineDispatcher
                    get() = testDispatchers
            }
        }
    }
}