package wottrich.github.io.smartchecklist.newchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.newchecklist.domain.model.NewChecklistModel

abstract class AddNewChecklistUseCase : KotlinResultUseCase<NewChecklistModel, Long?>()