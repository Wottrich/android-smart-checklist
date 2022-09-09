package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import kotlinx.coroutines.delay
import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.base.BaseViewModel
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result
import wottrich.github.io.tools.base.UseCase
import wottrich.github.io.tools.base.UseCase.None

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
            _uiEffect.emit(SplashUiEffect.GoToHome)
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
    override suspend fun execute(params: None): Result<Boolean> {
        return Result.success(checklistDao.getAllOldChecklistWithTasks().isNotEmpty())
    }
}