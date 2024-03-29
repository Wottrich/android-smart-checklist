package wottrich.github.io.smartchecklist.checklist.domain

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase

abstract class DeleteChecklistUseCase : KotlinResultUseCase<String, UseCase.Empty>()