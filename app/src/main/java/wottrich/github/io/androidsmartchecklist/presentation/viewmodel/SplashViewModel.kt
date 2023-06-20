package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.UseCase
import github.io.wottrich.coroutines.base.Result
import kotlinx.coroutines.delay
import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.base.BaseViewModel

class SplashViewModel(
    private val hasOldChecklistToMigrateUseCase: HasOldChecklistToMigrateUseCase
) : BaseViewModel() {

    private val _uiEffect = SingleShotEventBus<SplashUiEffect>()
    val uiEffect = _uiEffect.events

    init {
        launchIO {
            delay(SPLASH_DELAY)
            val hasOldChecklistToMigrate = hasOldChecklistToMigrateUseCase().getOrNull() ?: false
            val state = if (hasOldChecklistToMigrate) SplashUiEffect.GoToMigration
            else SplashUiEffect.GoToHome
            _uiEffect.emit(state)
        }
    }

    companion object {
        private const val SPLASH_DELAY = 1000L
    }
}

sealed class SplashUiEffect {
    object GoToHome : SplashUiEffect()
    object GoToMigration : SplashUiEffect()
}

class HasOldChecklistToMigrateUseCase(
    private val checklistDao: ChecklistDao
) : KotlinResultUseCase<UseCase.None, Boolean>() {
    override suspend fun execute(params: UseCase.None): Result<Boolean> {
        return Result.success(checklistDao.getAllOldChecklistWithTasks().isNotEmpty())
    }
}