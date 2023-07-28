package wottrich.github.io.smartchecklist.checklist.domain

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase

abstract class UpdateSelectedChecklistUseCase : KotlinResultUseCase<String, UseCase.Empty>()