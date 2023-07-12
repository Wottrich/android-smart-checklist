package github.io.wottrich.checklist.domain

import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.UseCase

abstract class DeleteChecklistUseCase : KotlinResultUseCase<String, UseCase.Empty>()