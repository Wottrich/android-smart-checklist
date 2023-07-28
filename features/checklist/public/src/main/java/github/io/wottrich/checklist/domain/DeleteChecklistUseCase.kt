package github.io.wottrich.checklist.domain

import wottrich.github.io.coroutines.KotlinResultUseCase
import wottrich.github.io.coroutines.UseCase

abstract class DeleteChecklistUseCase : KotlinResultUseCase<String, UseCase.Empty>()